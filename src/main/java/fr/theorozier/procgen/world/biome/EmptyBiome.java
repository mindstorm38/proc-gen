package fr.theorozier.procgen.world.biome;

public class EmptyBiome extends Biome {
	
	private static final BiomeWeatherRange EMPTY_WEATER = new BiomeWeatherRange(0f, 0f, 50f, 50f);
	
	public EmptyBiome(int uid, String identifier) {
		super(uid, identifier, 0f, 0f, EMPTY_WEATER, Biomes.NO_SURFACE);
	}
	
}
