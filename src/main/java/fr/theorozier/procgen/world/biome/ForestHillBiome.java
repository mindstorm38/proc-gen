package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.SurfaceCountExtraConfig;

public class ForestHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 20f, 30f, 100f);
	
	public ForestHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.29f, 12f, WEATHER);
		
		this.addFeature(Features.PLACEMENT, new PlacementFeatureConfig(
				new ConfiguredPlacement<>(Placements.SURFACE_COUNT_EXTRA, new SurfaceCountExtraConfig(10, 1, 0.2f)),
				new ConfiguredFeature<>(Features.TREE, FeatureConfig.EMPTY)
		));
		
	}
	
}
