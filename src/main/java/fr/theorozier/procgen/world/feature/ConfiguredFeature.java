package fr.theorozier.procgen.world.feature;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.WorldBlockPosition;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public class ConfiguredFeature<C extends FeatureConfig> {
	
	private Feature<C> feature;
	private C config;
	
	public ConfiguredFeature(Feature<C> feature, C config) {
		
		this.feature = feature;
		this.config = config;
		
	}
	
	public boolean place(World world, ChunkGenerator generator, Random rand, WorldBlockPosition at) {
		return this.feature.place(world, generator, rand, at, this.config);
	}
	
}
