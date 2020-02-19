package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.placement.config.ChanceConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;
import java.util.stream.Stream;

public class SurfaceChancePlacement extends SurfacePlacement<ChanceConfig> {
	
	@Override
	protected Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, ChanceConfig config) {
		
		if (rand.nextFloat() < config.getChance()) {
			return Stream.of(randomSurfacePosition(world, rand, at));
		} else {
			return Stream.empty();
		}
		
	}
	
}
