package fr.theorozier.procgen.world.biome;

public class PlainBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 35f, 10f, 90f);
	
	public PlainBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.28f, 5f, WEATHER);
		
	}
	
}
