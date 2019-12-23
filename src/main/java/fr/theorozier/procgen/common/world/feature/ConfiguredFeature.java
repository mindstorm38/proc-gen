package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;

public class ConfiguredFeature<C extends FeatureConfig> {
	
	private Feature<C> feature;
	private C config;
	
	public ConfiguredFeature(Feature<C> feature, C config) {
		
		this.feature = feature;
		this.config = config;
		
	}
	
	public boolean place(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at) {
		return this.feature.place(world, generator, rand, at, this.config);
	}
	
}
