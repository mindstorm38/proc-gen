package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.biome.BiomeAccessor;
import fr.theorozier.procgen.common.world.biome.Biome;

public abstract class BiomeProvider implements BiomeAccessor {

	protected final long seed;
	
	public BiomeProvider(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	@Override
	public abstract Biome getBiomeAt(int x, int z);

}
