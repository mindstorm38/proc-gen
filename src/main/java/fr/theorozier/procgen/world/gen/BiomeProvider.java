package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.biome.Biome;

public abstract class BiomeProvider {

	protected final long seed;
	
	public BiomeProvider(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public abstract Biome getBiomeAt(int x, int z);
	
	public Biome[] getBiomes(int x, int z, int dx, int dz) {
		
		Biome[] biomes = new Biome[dx * dz];
		
		int xMax = x + dx;
		int zMax = z + dz;
		
		for (; x < xMax; x++) {
			for (; z < zMax; z++) {
				biomes[x * dz + z] = getBiomeAt(x, z);
			}
		}
		
		return biomes;
		
	}

}
