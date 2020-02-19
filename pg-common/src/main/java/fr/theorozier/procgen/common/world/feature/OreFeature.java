package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.feature.config.OreFeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class OreFeature extends Feature<OreFeatureConfig> {
	
	private static boolean canPlaceOreOn(BlockState block) {
		return block.isBlock(Blocks.STONE);
	}
	
	@Override
	public boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, OreFeatureConfig config) {
		
		AtomicInteger oreCount = new AtomicInteger(config.getInfCount() + (int) (rand.nextFloat() * (config.getSupCount() - config.getInfCount())));
		
		place(world, rand, at, config, oreCount);
		return true;
		
	}
	
	private static void place(WorldAccessorServer world, Random rand, AbsBlockPosition at, OreFeatureConfig config, AtomicInteger oreCount) {
	
		if (oreCount.get() == 0)
			return;
		
		BlockState block = world.getBlockAt(at);
		
		if (block == null || !canPlaceOreOn(block))
			return;
		
		world.setBlockAt(at, config.getOre());
		
		oreCount.getAndDecrement();
	
		for (int i = 0; i < 3; ++i) {
			
			Direction dir = Direction.getRandom(rand.nextFloat());
			
			BlockPosition pos = new BlockPosition(at);
			pos.add(dir);
			
			place(world, rand, pos, config, oreCount);
			
		}
		
	}
	
}
