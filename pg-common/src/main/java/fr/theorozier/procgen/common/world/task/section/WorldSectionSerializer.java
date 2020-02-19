package fr.theorozier.procgen.common.world.task.section;

import fr.theorozier.procgen.common.util.io.ByteDataOutputStream;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import io.sutil.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class WorldSectionSerializer {

    public static final WorldSectionSerializer TEMP_INSTANCE = new WorldSectionSerializer();

    public static final Charset CHARSET = StringUtils.CHARSET_US_ASCII;

    public void serialize(WorldServerSection section, DataOutputStream stream) throws IOException {

        ByteDataOutputStream allChunksBuf = new ByteDataOutputStream(4096);
        ByteDataOutputStream chunkBuf = new ByteDataOutputStream(1024);

        WorldSectionBlockRegistry blockRegistry = new WorldSectionBlockRegistry();
        ByteArrayOutputStream rawChunkBuf;

        for (int y = 0; y < section.getWorld().getVerticalChunkCount(); ++y) {

            chunkBuf.reset();

            this.serializeChunk(section.getChunkAt(y), blockRegistry, chunkBuf);

            rawChunkBuf = chunkBuf.getByteStream();
            allChunksBuf.writeInt(rawChunkBuf.size());

            if (rawChunkBuf.size() != 0)
                rawChunkBuf.writeTo(allChunksBuf);

        }

        try {

            blockRegistry.foreachStates((state, uid) -> {

                try {

                    byte[] identifierBytes = state.getBlock().getIdentifier().getBytes();

                    stream.writeShort(uid);
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

    protected void serializeChunk(WorldChunk chunk, WorldSectionBlockRegistry blockRegistry, DataOutputStream buf) throws IOException {

        // Typical chunk buffer :
        //   [
        //     00,  \
        //     01,  -\ No block state for 1 length
        //     06,
        //     06,
        //     00,  \
        //     03,  -\ No block state for 3 length
        //     A1,
        //     00,  \
        //     00   -\ Marker for the end of the chunk, no block state remaining
        //   ]

        short[] data = chunk.getBlockData();

        short limitStart = -1;
        short val;

        for (short i = 0; i < 4096; ++i) {

            val = data[i];

            if (val == 0) {

                if (limitStart == -1) {
                    limitStart = i;
                    buf.writeShort(val);
                }

            } else {

                if (limitStart != -1) {
                    buf.writeShort((short) (i - limitStart));
                    limitStart = -1;
                }

                buf.writeShort(blockRegistry.getBlockStateUid(val));

            }

        }

    }

    public void deserialize(WorldServerSection section, InputStream stream) {

    }

}
