package fr.theorozier.procgen.common.world.serial;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.BlockStateProperty;
import fr.theorozier.procgen.common.util.array.supplier.ArraySupplier;
import fr.theorozier.procgen.common.util.io.ByteDataOutputStream;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.biome.Biomes;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.serial.registry.SaveShortRegistry;
import fr.theorozier.procgen.common.world.serial.rle.SectionBiomeRLE;
import fr.theorozier.procgen.common.world.serial.rle.SectionDataRLE;
import io.sutil.StringUtils;

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
		
		stream.writeShort(1);
		
		ByteDataOutputStream buf = new ByteDataOutputStream(1024);
		
		SectionBiomeRLE biomeRLE = new SectionBiomeRLE();
		SectionDataRLE dataRLE = new SectionDataRLE();
		
		// == Biomes
		SaveShortRegistry<Biome> biomeRegistry = new SaveShortRegistry<>();
		biomeRLE.encode(ArraySupplier.from(section.getBiomesData()), buf, biomeRegistry);
		
		stream.writeShort((short) biomeRegistry.size());
		
		for (Map.Entry<Biome, Short> biomeMapping : biomeRegistry.getMappingsEntries()) {
			
			byte[] identifierBytes = biomeMapping.getKey().getIdentifier().getBytes(CHARSET);
			
			stream.writeShort(biomeMapping.getValue());
			stream.writeInt(identifierBytes.length);
			stream.write(identifierBytes);
			
		}
		
		stream.writeInt(buf.size());
		buf.writeTo(stream);
		buf.reset();
		
		// == Chunk data
		ByteDataOutputStream allChunksBuf = new ByteDataOutputStream(4096);
		SaveShortRegistry<BlockState> stateRegistry = new SaveShortRegistry<>();
		int verticalChunkCount = section.getWorld().getVerticalChunkCount();

		for (int y = 0; y < verticalChunkCount; ++y) {

			this.serializeChunk(dataRLE, section.getChunkAt(y), stateRegistry, buf);
			allChunksBuf.writeInt(buf.getByteStream().size());
			buf.getByteStream().writeTo(allChunksBuf);
			
			buf.reset();

		}

		// Cast to short to store as "unsigned short".
		stream.writeShort((short) stateRegistry.size());
		
		for (Map.Entry<BlockState, Short> stateMapping : stateRegistry.getMappingsEntries()) {
			
			BlockState state = stateMapping.getKey();
			
			byte[] identifierBytes = state.getBlock().getIdentifier().getBytes();

			stream.writeShort(stateMapping.getValue());
			stream.writeInt(identifierBytes.length);
			stream.write(identifierBytes);
			stream.writeByte(state.getPropertiesCount()); // Using byte because state with so much properties can not be stored.
		
			for (Map.Entry<BlockStateProperty<?>, ?> statePropertyEntry : state.getProperties().entrySet()) {

				try {

					byte[] propertyBytes = statePropertyEntry.getKey().getName().getBytes(CHARSET);
					stream.writeInt(propertyBytes.length);
					stream.write(propertyBytes);

					propertyBytes = statePropertyEntry.getKey().getValueNameSafe(statePropertyEntry.getValue()).getBytes(CHARSET); // Should never throw cast exceptions
					stream.writeInt(propertyBytes.length);
					stream.write(propertyBytes);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			}

		}

		allChunksBuf.getByteStream().writeTo(stream);

	}

	protected void serializeChunk(SectionDataRLE dataRLE, WorldServerChunk chunk, SaveShortRegistry<BlockState> stateRegistry, DataOutputStream buf) throws IOException {

		// RLE (Run-Length Encoding).
		dataRLE.encode(ArraySupplier.from(chunk.getBlockData()), buf, stateRegistry);

	}

	/////////////////////
	// DESERIALIZATION //
	/////////////////////

	public void deserialize(WorldServerSection section, DataInputStream stream) throws IOException {
		
		try {
			
			int version = stream.readUnsignedShort();
			SectionBiomeRLE biomeRLE = new SectionBiomeRLE();
			SectionDataRLE dataRLE = new SectionDataRLE();
			byte[] buffer = null;
			
			// == Biomes
			Map<Short, Biome> mappedBiomes = new HashMap<>();
			int biomesCount = stream.readUnsignedShort();
			
			Biome biome;
			
			for (int i = 0; i < biomesCount; ++i) {
			
				short saveUid = stream.readShort();
				buffer = readStringBuffer(stream, buffer);
				String identifier = new String(buffer, CHARSET);
			
				biome = Biomes.getBiome(identifier);
				mappedBiomes.put(saveUid, biome == null ? Biomes.EMPTY : biome);
				
			}
			
			int biomesLength = stream.readInt();
			biomeRLE.decode(ArraySupplier.from(section.getBiomesData()), stream, biomesLength, mappedBiomes);
			
			// == Chunk data
			Map<Short, BlockState> mappedBlockStates = new HashMap<>();
			int statesCount = stream.readUnsignedShort();

			Block block;
			BlockState state;
			BlockStateProperty<?> property;

			for (int i = 0; i < statesCount; ++i) {

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
					deserializeChunk(dataRLE, section.getChunkAt(y), stream, chunkLength, mappedBlockStates);
				}

			}

		} catch (EOFException e) {
			throw new IOException("Wrong chunk format, End Of File should not happen right chunks.", e);
		}

	}

	protected void deserializeChunk(SectionDataRLE dataRLE, WorldServerChunk chunk, DataInputStream stream, int chunkLength, Map<Short, BlockState> mappedBlockStates) throws IOException {
		
		// RLE (Run-Length Encoding).
		dataRLE.decode(ArraySupplier.from(chunk.getBlockData()), stream, chunkLength, mappedBlockStates);

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
