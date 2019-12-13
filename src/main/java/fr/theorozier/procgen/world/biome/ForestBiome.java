package fr.theorozier.procgen.world.biome;

public class ForestBiome extends Biome {
	
	public ForestBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 12f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		addOres(this);
		addNormalForest(this);
		addPlantGrass(this);
		addBasicFlowers(this);
		
	}
	
}
