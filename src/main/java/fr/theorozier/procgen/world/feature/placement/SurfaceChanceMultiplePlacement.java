package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.feature.placement.config.ChanceCountConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceChanceMultiplePlacement extends SurfacePlacement<ChanceCountConfig> {
	
	@Override
	protected Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, ChanceCountConfig config) {
		
		return IntStream
				.range(0, config.getCount())
				.filter(i -> rand.nextFloat() < config.getChance())
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
		
	}
	
}
