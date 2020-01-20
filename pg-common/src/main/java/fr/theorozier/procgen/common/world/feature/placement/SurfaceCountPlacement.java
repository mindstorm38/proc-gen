package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.feature.placement.config.CountConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountPlacement extends SurfacePlacement<CountConfig> {
	
	@Override
	protected Stream<BlockPositioned> position(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, CountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
