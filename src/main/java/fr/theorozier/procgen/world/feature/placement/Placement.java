package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class Placement<C extends PlacementConfig> {
	
	protected abstract Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, C config);

	protected <FC extends FeatureConfig> boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, C config, ConfiguredFeature<FC> featureConfig) {
		
		AtomicBoolean placed = new AtomicBoolean(false);
		
		this.position(world, generator, rand, at, config).forEach(pos -> {
			
			if (featureConfig.place(world, generator, rand, pos))
				placed.set(true);
			
		});
		
		return placed.get();
	}
	
}
