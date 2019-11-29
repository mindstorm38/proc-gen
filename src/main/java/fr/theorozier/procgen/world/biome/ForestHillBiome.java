package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;

public class ForestHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 20f, 30f, 100f);
	
	public ForestHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 12f, WEATHER);
		
		this.addFeature(Features.TREE, FeatureConfig.EMPTY);
		
	}
	
}
