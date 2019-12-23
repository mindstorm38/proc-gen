package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.feature.placement.config.CountExtraConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountExtraPlacement extends SurfacePlacement<CountExtraConfig> {
	
	@Override
	protected Stream<BlockPositioned> position(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, CountExtraConfig config) {
	
		int count = config.getCount() + (rand.nextFloat() < config.getExtraChance() ? config.getExtraCount() : 0);
		
		return IntStream
				.range(0, count)
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
	
	}
	
}
