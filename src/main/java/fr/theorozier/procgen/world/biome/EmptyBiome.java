package fr.theorozier.procgen.world.biome;

public class EmptyBiome extends Biome {
	
	private static final BiomeWeatherRange EMPTY_WEATER = new BiomeWeatherRange(0f, 0f);
	
	public EmptyBiome(int uid, String identifier) {
		super(uid, identifier, 0f, 0f, Biomes.NO_SURFACE, Biomes.NO_SURFACE);
	}
	
}
