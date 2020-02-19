package fr.theorozier.procgen.common.world.util;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.AbsSectionPosition;

import java.util.Objects;

public class DummyWorld implements WorldAccessor {
	
	private final Biome biome;
	
	public DummyWorld(Biome biome) {
		this.biome = Objects.requireNonNull(biome, "Global biome can't be null.");
	}
	
	@Override
	public WorldSection getSectionAt(int x, int z) {
		return null;
	}

	@Override
	public WorldSection getSectionAt(AbsSectionPosition pos) {
		return null;
	}

	@Override
	public WorldSection getSectionAtBlock(int x, int z) {
		return null;
	}

	@Override
	public WorldSection getSectionAtBlock(AbsBlockPosition pos) {
		return null;
	}

	@Override
	public boolean isSectionLoadedAt(int x, int z) {
		return false;
	}

	@Override
	public boolean isSectionLoadedAt(AbsSectionPosition pos) {
		return false;
	}

	@Override
	public WorldChunk getChunkAt(int x, int y, int z) {
		return null;
	}

	@Override
	public WorldChunk getChunkAt(AbsBlockPosition pos) {
		return null;
	}

	@Override
	public WorldChunk getChunkAtBlock(int x, int y, int z) {
		return null;
	}

	@Override
	public WorldChunk getChunkAtBlock(AbsBlockPosition pos) {
		return null;
	}

	@Override
	public int getVerticalChunkCount() {
		return 1;
	}

	@Override
	public int getHeightLimit() {
		return 16;
	}

	@Override
	public Biome getBiomeAt(int x, int z) {
		return this.biome;
	}

	@Override
	public Biome getBiomeAt(AbsSectionPosition pos) {
		return null;
	}

	@Override
	public BlockState getBlockAt(int x, int y, int z) {
		return null;
	}

	@Override
	public BlockState getBlockAt(AbsBlockPosition pos) {
		return null;
	}

	@Override
	public void setBlockAt(int x, int y, int z, BlockState state) { }

	@Override
	public void setBlockAt(AbsBlockPosition pos, BlockState state) { }

	@Override
	public boolean isBlockAt(int x, int y, int z, BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockAt(AbsBlockPosition pos, BlockState state) {
		return false;
	}

}
