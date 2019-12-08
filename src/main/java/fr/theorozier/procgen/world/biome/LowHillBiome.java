package fr.theorozier.procgen.world.biome;

public class LowHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(15f, 20f, 60f, 90f);
	
	public LowHillBiome(int uid, String identifier) {
		
		super(uid, identifier, /*0.30f*/ 0.20f, 24f, WEATHER, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		addOres(this);
		addBasicFlowers(this);
		
	}
	
}
