package fr.theorozier.procgen.common.world.util;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;

import java.util.Objects;

public class DummyWorld implements WorldAccessor {
	
	private final Biome biome;
	
	public DummyWorld(Biome biome) {
		
		this.biome = Objects.requireNonNull(biome, "Global biome can't be null.");
		
	}
	
	@Override
	public WorldChunk getChunkAt(int x, int y, int z) {
		return null;
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		return this.biome;
	}
	
	@Override
	public BlockState getBlockAt(int x, int y, int z) {
		return null;
	}
	
	@Override
	public void setBlockAt(int x, int y, int z, BlockState state) {
	
	}
	
}
