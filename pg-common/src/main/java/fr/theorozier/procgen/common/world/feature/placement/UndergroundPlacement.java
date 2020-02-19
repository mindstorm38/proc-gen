package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.placement.config.UndergroundConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UndergroundPlacement extends Placement<UndergroundConfig> {
	
	@Override
	protected Stream<AbsBlockPosition> position(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, UndergroundConfig config) {
		
		return IntStream
				.range(0, config.getChanceCount())
				.filter(i -> rand.nextFloat() < config.getChance())
				.mapToObj(i -> {
					
					int xRand = rand.nextInt(16);
					int zRand = rand.nextInt(16);
					int yRand = config.getBottomOffset() + rand.nextInt(config.getTopOffset() - config.getBottomOffset());
					
					return new ImmutableBlockPosition(at.getX() + xRand, at.getY() + yRand, at.getZ() + zRand);
					
				});
		
	}
	
}
