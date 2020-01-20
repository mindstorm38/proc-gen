package fr.theorozier.procgen.common.world.gen.biome;

import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.biome.BiomeAccessor;

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
