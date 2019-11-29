package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public class ConfiguredPlacement<C extends PlacementConfig> {
	
	private final Placement<C> placement;
	private final C config;
	
	public ConfiguredPlacement(Placement<C> placement, C config) {
		
		this.placement = placement;
		this.config = config;
		
	}
	
	public <FC extends FeatureConfig> boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, ConfiguredFeature<FC> featureConfig) {
		return placement.place(world, generator, rand, at, this.config, featureConfig);
	}
	
}
