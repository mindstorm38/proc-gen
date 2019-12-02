package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.CountExtraConfig;

public class ForestBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 20f, 30f, 100f);
	
	public ForestBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.29f, 12f, WEATHER, Biomes.GRASS_SURFACE);
		
		this.addPlacedFeature(
				Placements.SURFACE_COUNT_EXTRA,
				new CountExtraConfig(10, 1, 0.2f),
				Features.TREE,
				FeatureConfig.EMPTY
		);
		
		addOres(this);
		
	}
	
}
