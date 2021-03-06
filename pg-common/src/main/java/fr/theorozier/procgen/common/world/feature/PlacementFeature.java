package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;

public class PlacementFeature extends Feature<PlacementFeatureConfig> {
	
	@Override
	public boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, PlacementFeatureConfig config) {
		return config.getPlacement().place(world, generator, rand, at, config.getFeature());
	}
	
}
