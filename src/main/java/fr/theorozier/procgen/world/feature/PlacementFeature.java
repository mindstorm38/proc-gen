package fr.theorozier.procgen.world.feature;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public class PlacementFeature extends Feature<PlacementFeatureConfig> {
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, PlacementFeatureConfig config) {
		return config.getPlacement().place(world, generator, rand, at, config.getFeature());
	}
	
}
