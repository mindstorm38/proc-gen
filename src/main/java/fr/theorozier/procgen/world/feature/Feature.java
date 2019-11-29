package fr.theorozier.procgen.world.feature;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public abstract class Feature<C extends FeatureConfig> {

	public abstract boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, C config);
	
}
