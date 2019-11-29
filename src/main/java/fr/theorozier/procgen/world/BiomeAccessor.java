package fr.theorozier.procgen.world;

import fr.theorozier.procgen.world.biome.Biome;

public interface BiomeAccessor {

	Biome getBiomeAt(int x, int z);
	Biome getBiomeAt(BlockPosition pos);
	
	default Biome[] getBiomes(int x, int z, int dx, int dz) {
		
		Biome[] biomes = new Biome[dx * dz];
		
		for (int rx = 0; rx < dx; rx++) {
			for (int rz = 0; rz < dz; rz++) {
				biomes[rx * dz + rz] = getBiomeAt(x + rx, z + rz);
			}
		}
		
		return biomes;
		
	}

}
