package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.biome.BiomeAccessor;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.chunk.SectionPosition;

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
