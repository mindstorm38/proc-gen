package fr.theorozier.procgen.world.biome;

public class BiomeRiver extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(0.8f, 0.48f);
	
	public BiomeRiver(int uid, String identifier) {
		
		super(uid, identifier, 0.23f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
	}
	
}
