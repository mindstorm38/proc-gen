package fr.theorozier.procgen.world.biome;

public class DesertHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(15f, 50f, 0f, 10f);
	
	public DesertHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.30f, 24f, WEATHER, Biomes.DESERT_SURFACE);
		
		addOres(this);
		
	}
	
}
