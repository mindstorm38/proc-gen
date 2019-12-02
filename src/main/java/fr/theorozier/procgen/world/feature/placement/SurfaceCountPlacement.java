package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.feature.placement.config.CountConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountPlacement extends SurfacePlacement<CountConfig> {
	
	@Override
	protected Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, CountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
