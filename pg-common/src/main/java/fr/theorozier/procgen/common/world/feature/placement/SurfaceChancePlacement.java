package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.feature.placement.config.ChanceConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.Random;
import java.util.stream.Stream;

public class SurfaceChancePlacement extends SurfacePlacement<ChanceConfig> {
	
	@Override
	protected Stream<BlockPositioned> position(WorldDimension world, ChunkGenerator generator, Random rand, BlockPositioned at, ChanceConfig config) {
		
		if (rand.nextFloat() < config.getChance()) {
			return Stream.of(randomSurfacePosition(world, rand, at));
		} else {
			return Stream.empty();
		}
		
	}
	
}
