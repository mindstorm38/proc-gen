package fr.theorozier.procgen.world.feature.config;

import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;

public class PlacementFeatureConfig implements FeatureConfig {

	private final ConfiguredPlacement<?> placement;
	private final ConfiguredFeature<?> feature;
	
	public PlacementFeatureConfig(ConfiguredPlacement<?> placement, ConfiguredFeature<?> feature) {
		
		this.placement = placement;
		this.feature = feature;
		
	}
	
	public ConfiguredPlacement<?> getPlacement() {
		return this.placement;
	}
	
	public ConfiguredFeature<?> getFeature() {
		return this.feature;
	}

}
