package fr.theorozier.procgen.common.world.biome;

import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

public interface BiomeAccessor {

	Biome getBiomeAt(int x, int z);
	
	default Biome getBiomeAt(SectionPositioned pos) {
		return this.getBiomeAt(pos.getX(), pos.getZ());
	}
	
	default Biome[] getBiomes(int x, int z, int dx, int dz) {
		
		Biome[] biomes = new Biome[dx * dz];
		
		for (int rx = 0; rx < dx; rx++) {
			for (int rz = 0; rz < dz; rz++) {
				biomes[WorldSection.getSectionIndex(rx, rz)] = getBiomeAt(x + rx, z + rz);
			}
		}
		
		return biomes;
		
	}

}
