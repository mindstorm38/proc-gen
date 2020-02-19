package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;

public abstract class Feature<C extends FeatureConfig> {

	public abstract boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, C config);
	
}
