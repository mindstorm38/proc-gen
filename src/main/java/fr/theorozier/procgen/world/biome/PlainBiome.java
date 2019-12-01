package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.ChanceConfig;

public class PlainBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(-5f, 35f, 10f, 90f);
	
	public PlainBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.28f, 5f, WEATHER);
		
		this.addFeature(Features.PLACEMENT, new PlacementFeatureConfig(
				new ConfiguredPlacement<>(Placements.SURFACE_CHANCE, new ChanceConfig(0.2f)),
				new ConfiguredFeature<>(Features.TREE, FeatureConfig.EMPTY)
		));
		
	}
	
}
