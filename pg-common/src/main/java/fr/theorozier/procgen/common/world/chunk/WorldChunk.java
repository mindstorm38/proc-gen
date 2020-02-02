package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.buffer.VariableBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Object that hold a chunk of (x*y*z) 16*[world height]*16
 * @author Theo Rozier
 *
 */
public class WorldChunk {

	private final WorldBase world;
	private final WorldSection section;
	private final ImmutableBlockPosition position;
	private final ImmutableBlockPosition centerBlockPosition;
	
	private final short[] data;
	
	public WorldChunk(WorldBase world, WorldSection section, ImmutableBlockPosition position) {
		
		this.world = world;
		this.section = section;
		this.position = position;
		this.centerBlockPosition = new ImmutableBlockPosition((position.getX() << 4) + 7, (position.getY() << 4) + 7, (position.getZ() << 4) + 7);
		
		this.data = new short[4096];
		
	}
	
	// PROPERTIES //
	
	public WorldBase getWorld() {
		return this.world;
	}
	
	public WorldSection getSection() {
		return this.section;
	}
	
	public final ImmutableBlockPosition getChunkPos() {
		return this.position;
	}
	
	public float getDistSquaredTo(float x, float y, float z) {
		return this.centerBlockPosition.distSquared(x, y, z);
	}
	
	// BIOMES //
	
	public Biome getBiomeAt(int x, int z) {
		return this.section.getBiomeAt(x, z);
	}
	
	public Biome getBiomeAtBlock(int x, int z) {
		return this.section.getBiomeAtBlock(x, z);
	}
	
	public Biome getBiomeAtBlock(SectionPositioned pos) {
		return this.section.getBiomeAtBlock(pos);
	}
	
	// BLOCKS //
	
	public BlockState getBlockAt(int x, int y, int z) {
		return Blocks.getBlockState(this.data[getBlockIndex(x, y, z)]);
	}
	
	public void setBlockAt(int x, int y, int z, BlockState state) {
		this.data[getBlockIndex(x, y, z)] = state.getBlock().isUnsavable() ? 0 : state.getUid();
	}
	
	// SAVING //
	
	public void saveChunk(WorldSectionBlockRegistry blockRegistry, VariableBuffer chunkBuf) {
		
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
		
		short limitStart = -1;
		short val;
		
		for (short i = 0; i < 4096; ++i) {
			
			val = this.data[i];
			
			if (val == 0) {
				
				if (limitStart == -1) {
					limitStart = i;
					chunkBuf.writeShort(val);
				}
				
			} else {
				
				if (limitStart != -1) {
					chunkBuf.writeShort((short) (i - limitStart));
					limitStart = -1;
				}
				
				chunkBuf.writeShort(blockRegistry.getBlockStateUid(val));
				
			}
			
		}
	
	}
	
	// UTILS //
	
	public static int getBlockIndex(int x, int y, int z) {
		return x * 256 + y * 16 + z;
	}
	
}
