package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class Placement<C extends PlacementConfig> {
	
	protected abstract Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, C config);

	protected <FC extends FeatureConfig> boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, C config, ConfiguredFeature<FC> featureConfig) {
		
		AtomicBoolean placed = new AtomicBoolean(false);
		
		this.position(world, generator, rand, at, config).forEach(pos -> {
			
			if (featureConfig.place(world, generator, rand, pos))
				placed.set(true);
			
		});
		
		return placed.get();
	}
	
}
