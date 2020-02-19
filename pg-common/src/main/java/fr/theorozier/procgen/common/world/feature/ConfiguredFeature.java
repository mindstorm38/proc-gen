package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;

public class ConfiguredFeature<C extends FeatureConfig> {
	
	private Feature<C> feature;
	private C config;
	
	public ConfiguredFeature(Feature<C> feature, C config) {
		
		this.feature = feature;
		this.config = config;
		
	}
	
	public boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at) {
		return this.feature.place(world, generator, rand, at, this.config);
	}
	
	@Override
	public String toString() {
		return "ConfiguredFeature{" +
				"feature=" + feature +
				", config=" + config +
				'}';
	}
	
}
