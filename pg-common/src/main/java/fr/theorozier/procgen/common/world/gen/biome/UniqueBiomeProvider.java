package fr.theorozier.procgen.common.world.gen.biome;

import fr.theorozier.procgen.common.world.biome.Biome;

import java.util.Objects;

public class UniqueBiomeProvider extends BiomeProvider {
	
	private final Biome biome;
	
	public UniqueBiomeProvider(long seed, Biome biome) {
		super(seed);
		this.biome = Objects.requireNonNull(biome);
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		return this.biome;
	}
	
}
