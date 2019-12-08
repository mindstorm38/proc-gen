package fr.theorozier.procgen.world.biome;

public class ForestBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(10f, 15f, 30f, 100f);
	
	public ForestBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.29f, 12f, WEATHER, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		addNormalForest(this);
		addBasicFlowers(this);
		addOres(this);
		
	}
	
}
