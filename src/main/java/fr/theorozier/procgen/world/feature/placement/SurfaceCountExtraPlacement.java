package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.feature.placement.config.SurfaceCountExtraConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SurfaceCountExtraPlacement extends SurfacePlacement<SurfaceCountExtraConfig> {
	
	@Override
	protected Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, SurfaceCountExtraConfig config) {
	
		int count = config.getCount() + (rand.nextFloat() < config.getExtraChance() ? config.getExtraCount() : 0);
		
		return IntStream
				.range(0, count)
				.mapToObj(i -> randomSurfacePosition(world, rand, at));
	
	}
	
}
