package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

public interface WorldAccessor {

	// CHUNKS //
	
	/**
	 * Method to get a chunk at specific position.
	 * @param x The X chunk coordinate.
	 * @param y The Y chunk coordinate.
	 * @param z The Z chunk coordinate.
	 * @return Chunk at this position, or <b>NULL</b> if no chunk there.
	 */
	WorldChunk getChunkAt(int x, int y, int z);
	
	default WorldChunk getChunkAt(BlockPositioned pos) {
		return this.getChunkAt(pos.getX(), pos.getY(), pos.getZ());
	}
	
	default WorldChunk getChunkAtBlock(int x, int y, int z) {
		return this.getChunkAt(x >> 4, y >> 4, z >> 4);
	}
	
	default WorldChunk getChunkAtBlock(BlockPositioned pos) {
		return this.getChunkAtBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	// BIOMES //
	
	Biome getBiomeAt(int x, int z);
	
	default Biome getBiomeAt(SectionPositioned pos) {
		return this.getBiomeAt(pos.getX(), pos.getZ());
	}
	
	// BLOCKS //
	
	BlockState getBlockAt(int x, int y, int z);
	
	default BlockState getBlockAt(BlockPositioned pos) {
		return this.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
	}
	
	void setBlockAt(int x, int y, int z, BlockState state);
	
	default void setBlockAt(BlockPositioned pos, BlockState state) {
		this.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), state);
	}

}
