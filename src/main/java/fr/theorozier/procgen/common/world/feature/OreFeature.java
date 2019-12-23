package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.common.world.feature.config.OreFeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class OreFeature extends Feature<OreFeatureConfig> {
	
	private static boolean canPlaceOreOn(WorldBlock block) {
		return block.getBlockType() == Blocks.STONE;
	}
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, OreFeatureConfig config) {
		
		AtomicInteger oreCount = new AtomicInteger(config.getInfCount() + (int) (rand.nextFloat() * (config.getSupCount() - config.getInfCount())));
		
		place(world, rand, at, config, oreCount);
		return true;
		
	}
	
	private static void place(World world, Random rand, BlockPosition at, OreFeatureConfig config, AtomicInteger oreCount) {
	
		if (oreCount.get() == 0)
			return;
		
		WorldBlock block = world.getBlockAt(at);
		
		if (block == null || !canPlaceOreOn(block))
			return;
		
		block.setBlockType(config.getOre());
		
		oreCount.getAndDecrement();
	
		for (int i = 0; i < 3; ++i) {
			
			Direction dir = Direction.getRandom(rand.nextFloat());
			place(world, rand, at.add(dir), config, oreCount);
			
		}
		
	}
	
}
