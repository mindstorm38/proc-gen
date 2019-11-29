package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.chunk.Section;
import fr.theorozier.procgen.world.chunk.SectionPosition;

public interface BiomeAccessor {

	Biome getBiomeAt(int x, int z);
	Biome getBiomeAt(SectionPosition pos);
	
	default Biome getBiomeAt(BlockPosition pos) {
		return this.getBiomeAt(pos.toSectionPosition());
	}
	
	default Biome[] getBiomes(int x, int z, int dx, int dz) {
		
		Biome[] biomes = new Biome[dx * dz];
		
		for (int rx = 0; rx < dx; rx++) {
			for (int rz = 0; rz < dz; rz++) {
				biomes[Section.getHorizontalPositionIndex(rx, rz)] = getBiomeAt(x + rx, z + rz);
			}
		}
		
		return biomes;
		
	}

}
