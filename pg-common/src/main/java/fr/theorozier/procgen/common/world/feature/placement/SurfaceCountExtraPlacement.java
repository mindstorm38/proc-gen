package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.placement.config.CountExtraConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountExtraPlacement extends SurfacePlacement<CountExtraConfig> {
	
	@Override
	protected Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, CountExtraConfig config) {
	
		int count = config.getCount() + (rand.nextFloat() < config.getExtraChance() ? config.getExtraCount() : 0);
		
		return IntStream
				.range(0, count)
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
	
	}
	
}
