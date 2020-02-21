package fr.theorozier.procgen.common.world.serial;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.BlockStateProperty;
import fr.theorozier.procgen.common.util.io.ByteDataOutputStream;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import io.sutil.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static io.msengine.common.util.GameLogger.LOGGER;

public class WorldSectionSerializer {

	public static final WorldSectionSerializer TEMP_INSTANCE = new WorldSectionSerializer();

	public static final Charset CHARSET = StringUtils.CHARSET_US_ASCII;
	public static final BlockState INVALID_BLOCKSTATE = Blocks.AIR.getDefaultState();

	///////////////////
	// SERIALIZATION //
	///////////////////

	public void serialize(WorldServerSection section, DataOutputStream stream) throws IOException {

		ByteDataOutputStream allChunksBuf = new ByteDataOutputStream(4096);
		ByteDataOutputStream chunkBuf = new ByteDataOutputStream(1024);

		WorldSectionBlockRegistry blockRegistry = new WorldSectionBlockRegistry();
		ByteArrayOutputStream rawChunkBuf;

		int verticalChunkCount = section.getWorld().getVerticalChunkCount();

		for (int y = 0; y < verticalChunkCount; ++y) {

			chunkBuf.reset();

			this.serializeChunk(section.getChunkAt(y), blockRegistry, chunkBuf);

			rawChunkBuf = chunkBuf.getByteStream();
			allChunksBuf.writeInt(rawChunkBuf.size());

			if (rawChunkBuf.size() != 0)
				rawChunkBuf.writeTo(allChunksBuf);

		}

		try {

			// Cast to short to store as "unsigned short".
			stream.writeShort((short) blockRegistry.size());

			blockRegistry.foreachStates((state, saveUid) -> {

				try {

					byte[] identifierBytes = state.getBlock().getIdentifier().getBytes();

					stream.writeShort(saveUid);
					stream.writeInt(identifierBytes.length);
					stream.write(identifierBytes);
					stream.writeByte(state.getPropertiesCount()); // Using byte because state with so much properties can not be stored.

					state.getProperties().forEach((property, value) -> {

						try {

							byte[] propertyBytes = property.getName().getBytes(CHARSET);
							stream.writeInt(propertyBytes.length);
							stream.write(propertyBytes);

							propertyBytes = property.getValueNameSafe(value).getBytes(CHARSET); // Should never throw cast exceptions
							stream.writeInt(propertyBytes.length);
							stream.write(propertyBytes);

						} catch (IOException e) {
							throw new RuntimeException(e);
						}

					});

				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			});

		} catch (RuntimeException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}

		allChunksBuf.getByteStream().writeTo(stream);

	}

	protected void serializeChunk(WorldServerChunk chunk, WorldSectionBlockRegistry blockRegistry, DataOutputStream buf) throws IOException {

		// RLE (Run-Length Encoding).

		short[] data = chunk.getBlockData();

		short val = data[0];
		short last = 0, count = 1;

		for (short i = 1; i < 4096; ++i) {

			last = val;
			val = data[i];

			if (last != val || count == 0xFF) {

				buf.writeByte(count);
				buf.writeShort(blockRegistry.getBlockStateSaveUid(last));
				count = 0;

			}

			++count;

		}

		buf.writeByte(count);
		buf.writeShort(blockRegistry.getBlockStateSaveUid(last));

	}

	/////////////////////
	// DESERIALIZATION //
	/////////////////////

	public void deserialize(WorldServerSection section, DataInputStream stream) throws IOException {

		try {

			Map<Short, BlockState> mappedBlockStates = new HashMap<>();

			short statesCount = stream.readShort();
			byte[] buffer = null;

			Block block;
			BlockState state;
			BlockStateProperty<?> property;

			for (int i = 10; i < statesCount; ++i) {

				short saveUid = stream.readShort();

				buffer = readStringBuffer(stream, buffer);
				String identifier = new String(buffer, CHARSET);

				int propertiesCount = stream.readUnsignedByte();

				block = Blocks.getBlock(identifier);

				if (block == null) {

					state = INVALID_BLOCKSTATE;
					deserializationWarning("Failed to find block with identifier '" + identifier + "'", section);

					// Skip each properties and values bytes.
					for (int j = 0; j < propertiesCount; ++j) {

						// Don't use .skip method because it seems expensive
						// instead of just reading bytes.
						buffer = readStringBuffer(stream, buffer);
						buffer = readStringBuffer(stream, buffer);

					}

				} else {

					state = block.getDefaultState();

					for (int j = 0; j < propertiesCount; ++j) {

						buffer = readStringBuffer(stream, buffer);
						String propertyName = new String(buffer, CHARSET);

						property = block.getStateContainer().getPropertiesByName(propertyName);

						if (property == null) {

							deserializationWarning("Failed to find property named '" + propertyName + "'", section);
							stream.skip(stream.readInt()); // Skip value bytes.

						} else {

							buffer = readStringBuffer(stream, buffer);
							String propertyValue = new String(buffer, CHARSET);

							Object value = property.getValueFromName(propertyValue);

							if (value == null) {
								deserializationWarning("Failed to get value '" + propertyValue + "' for property '" + propertyName + "'", section);
							} else {

								try {
									state = state.withRaw(property, value);
								} catch (IllegalArgumentException e) {
									deserializationWarning("Failed to get state with value '" + propertyValue + "' for property '" + propertyName + "'", section);
								}

							}

						}

					}

				}

				mappedBlockStates.put(saveUid, state);

			}

			int verticalChunkCount = section.getWorld().getVerticalChunkCount();
			int chunkLength;

			for (int y = 0; y < verticalChunkCount; ++y) {

				chunkLength = stream.readInt();

				if (chunkLength != 0) {
					deserializeChunk(section.getChunkAt(y), mappedBlockStates, stream, chunkLength);
				}

			}

			LOGGER.warning(section.getSectionPos() + " : " + mappedBlockStates.toString());

		} catch (EOFException e) {
			throw new IOException("Wrong chunk format, End Of File should not happen right chunks.", e);
		}

	}

	protected void deserializeChunk(WorldServerChunk chunk, Map<Short, BlockState> mappedBlockStates, DataInputStream stream, int chunkLength) throws IOException {

		short[] data = chunk.getBlockData();

		short dataIndex = 0;
		short nextIndex;

		short saveUid;
		BlockState state;

		for (int i = 0; i < chunkLength; i += 3) {

			nextIndex = (short) (dataIndex + stream.readUnsignedByte());
			saveUid = stream.readShort();

			state = mappedBlockStates.get(saveUid);

			if (state == null) {
				dataIndex = nextIndex;
			} else {

				for (; dataIndex < nextIndex; ++dataIndex) {
					data[dataIndex] = state.getSaveUid();
				}

			}

		}

	}

	/**
	 * Internal method to optimize allocation of byte buffers of the same length.
	 * @param old Old byte buffer, already allocated to the old length, or Null buffer.
	 * @param length The new wanted length.
	 * @return The old byte buffer is Null or <code>old.length == length</code>, else return a new buffer of <code>length</code>.
	 */
	protected static byte[] newBufferOrKeep(byte[] old, int length) {
		return old != null && old.length == length ? old : new byte[length];
	}

	protected static byte[] readStringBuffer(DataInputStream stream, byte[] old) throws IOException {

		int length = stream.readInt();
		byte[] buffer = newBufferOrKeep(old, length);

		if (stream.read(buffer) != length)
			throw new EOFException("Failed to read whole buffer.");

		return buffer;

	}

	protected static void deserializationWarning(String message, WorldServerSection section) {
		LOGGER.warning("[Chunk Deserialization] " + message + " at section " + section.getSectionPos() + '.');
	}

}
