package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.AbsSectionPosition;

public interface WorldAccessor {

	// SECTIONS //
	
	/**
	 * Internal method to get a section at specific position.
	 * @param x The X section coordinate.
	 * @param z The Z section coordinate.
	 * @return Section at this position, or <b>NULL</b> if no section loaded there.
	 */
	WorldSection getSectionAt(int x, int z);
	
	/**
	 * Internal method to get a section a specific position.
	 * @param pos The section position.
	 * @return Section at this position, or <b>NULL</b> if no section loaded there.
	 */
	default WorldSection getSectionAt(AbsSectionPosition pos) {
		return this.getSectionAt(pos.getX(), pos.getZ());
	}
	
	/**
	 * Internal method to get a section at specific block position.
	 * @param x The X block coordinate.
	 * @param z The Y block coordinate.
	 * @return Section where the block is positionned, or <b>NULL</b> if no section loaded there.
	 */
	default WorldSection getSectionAtBlock(int x, int z) {
		return this.getSectionAt(x >> 4, z >> 4);
	}
	
	default WorldSection getSectionAtBlock(AbsBlockPosition pos) {
		return this.getSectionAtBlock(pos.getX(), pos.getZ());
	}
	
	boolean isSectionLoadedAt(int x, int z);
	
	default boolean isSectionLoadedAt(AbsSectionPosition pos) {
		return this.isSectionLoadedAt(pos.getX(), pos.getZ());
	}
	
	// CHUNKS //
	
	/**
	 * Method to get a chunk at specific position.
	 * @param x The X chunk coordinate.
	 * @param y The Y chunk coordinate.
	 * @param z The Z chunk coordinate.
	 * @return Chunk at this position, or <b>NULL</b> if no chunk there.
	 */
	WorldChunk getChunkAt(int x, int y, int z);
	
	default WorldChunk getChunkAt(AbsBlockPosition pos) {
		return this.getChunkAt(pos.getX(), pos.getY(), pos.getZ());
	}
	
	default WorldChunk getChunkAtBlock(int x, int y, int z) {
		return this.getChunkAt(x >> 4, y >> 4, z >> 4);
	}
	
	default WorldChunk getChunkAtBlock(AbsBlockPosition pos) {
		return this.getChunkAtBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * @return Number of chunks in a section's height, Must return unsigned byte (0-255).
	 */
	short getVerticalChunkCount();

	/**
	 * @return The height limit of the world.
	 */
	default int getHeightLimit() {
		return this.getVerticalChunkCount() * 16;
	}

	// BIOMES //
	
	Biome getBiomeAt(int x, int z);
	
	default Biome getBiomeAt(AbsSectionPosition pos) {
		return this.getBiomeAt(pos.getX(), pos.getZ());
	}
	
	// BLOCKS //
	
	BlockState getBlockAt(int x, int y, int z);
	
	default BlockState getBlockAt(AbsBlockPosition pos) {
		return this.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
	}
	
	void setBlockAt(int x, int y, int z, BlockState state);
	
	default void setBlockAt(AbsBlockPosition pos, BlockState state) {
		this.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), state);
	}
	
	boolean isBlockAt(int x, int y, int z, BlockState state);
	
	default boolean isBlockAt(AbsBlockPosition pos, BlockState state) {
		return this.isBlockAt(pos.getX(), pos.getY(), pos.getZ(), state);
	}

}
