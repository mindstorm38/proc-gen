package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.feature.placement.config.ChanceCountConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceChanceMultiplePlacement extends SurfacePlacement<ChanceCountConfig> {
	
	@Override
	protected Stream<BlockPositioned> position(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, ChanceCountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.filter(i -> rand.nextFloat() < config.getChance())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
