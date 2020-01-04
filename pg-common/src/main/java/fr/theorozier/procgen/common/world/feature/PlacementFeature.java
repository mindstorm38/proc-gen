package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.Random;

public class PlacementFeature extends Feature<PlacementFeatureConfig> {
	
	@Override
	public boolean place(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, PlacementFeatureConfig config) {
		return config.getPlacement().place(world, generator, rand, at, config.getFeature());
	}
	
}
