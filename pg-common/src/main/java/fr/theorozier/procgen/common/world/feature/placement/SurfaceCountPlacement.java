package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.placement.config.CountConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountPlacement extends SurfacePlacement<CountConfig> {
	
	@Override
	protected Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, CountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
