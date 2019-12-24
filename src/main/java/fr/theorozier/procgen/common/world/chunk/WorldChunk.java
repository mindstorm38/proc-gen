package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

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
	
	// UTILS //
	
	public static int getBlockIndex(int x, int y, int z) {
		return x * 256 + y * 16 + z;
	}
	
}
