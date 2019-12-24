package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;

public abstract class Feature<C extends FeatureConfig> {

	public abstract boolean place(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, C config);
	
}
