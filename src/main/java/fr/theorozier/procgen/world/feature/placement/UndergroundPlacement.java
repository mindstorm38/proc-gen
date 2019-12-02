package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.feature.placement.config.UndergroundConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UndergroundPlacement extends Placement<UndergroundConfig> {
	
	@Override
	protected Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, UndergroundConfig config) {
		
		return IntStream
				.range(0, config.getChanceCount())
				.filter(i -> rand.nextFloat() < config.getChance())
				.mapToObj(i -> {
					
					int xRand = rand.nextInt(16);
					int zRand = rand.nextInt(16);
					int yRand = config.getBottomOffset() + rand.nextInt(config.getTopOffset() - config.getBottomOffset());
					
					return at.add(xRand, yRand, zRand);
					
				});
		
	}
	
}
