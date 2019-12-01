package fr.theorozier.procgen.world.biome;

public class LowHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 30f, 40f, 100f);
	
	public LowHillBiome(int uid, String identifier) {
		super(uid, identifier, 0.30f, 24f, WEATHER);
	}
	
}
