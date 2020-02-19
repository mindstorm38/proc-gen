package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;

public class ConfiguredPlacement<C extends PlacementConfig> {
	
	private final Placement<C> placement;
	private final C config;
	
	public ConfiguredPlacement(Placement<C> placement, C config) {
		
		this.placement = placement;
		this.config = config;
		
	}
	
	public <FC extends FeatureConfig> boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, ConfiguredFeature<FC> featureConfig) {
		return placement.place(world, generator, rand, at, this.config, featureConfig);
	}
	
}
