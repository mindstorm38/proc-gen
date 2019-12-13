package fr.theorozier.procgen.world.biome;

public class ForestBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(0.7f, 0.8f);
	
	public ForestBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 12f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		addNormalForest(this);
		addBasicFlowers(this);
		addOres(this);
		
	}
	
}
