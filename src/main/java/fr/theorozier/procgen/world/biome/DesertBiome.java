package fr.theorozier.procgen.world.biome;

public class DesertBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(15f, 50f, 0f, 10f);
	
	public DesertBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.28f, 8f, WEATHER, Biomes.DESERT_SURFACE);
		
		addOres(this);
		
	}
	
}
