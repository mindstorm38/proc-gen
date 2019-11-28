package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.Features;

public class PlainsBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 30f, 10f, 90f);
	
	public PlainsBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.1f, 0.5f, WEATHER);
		
		this.addFeature(Features.TREE, Features.NO_CONFIG);
		
	}
	
}
