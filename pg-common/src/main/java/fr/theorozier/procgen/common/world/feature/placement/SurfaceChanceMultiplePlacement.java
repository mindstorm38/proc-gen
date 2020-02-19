package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.placement.config.ChanceCountConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceChanceMultiplePlacement extends SurfacePlacement<ChanceCountConfig> {
	
	@Override
	protected Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, ChanceCountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.filter(i -> rand.nextFloat() < config.getChance())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
