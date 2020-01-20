package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.Random;

public class TreeFeature extends Feature<FeatureConfig> {
	
	private static boolean canGrowOn(BlockState block) {
		return block != null && (block.isBlock(Blocks.DIRT) || block.isBlock(Blocks.GRASS));
	}
	
	private static boolean isLogBlock(BlockState block) {
		return block != null && block.isBlock(Blocks.LOG);
	}
	
	@Override
	public boolean place(WorldServer world, ChunkGenerator generator, Random rand, BlockPositioned at, FeatureConfig config) {
		
		BlockPosition temp = new BlockPosition();
		
		BlockState bottomBlock = world.getBlockAt(temp.set(at, 0, -1, 0));
		
		if (!canGrowOn(bottomBlock))
			return false;
		
		if (world.getBlockAt(temp.set(at, 0, 1, 0)) != null)
			return false;
		
		int height = this.getRandomHeight(rand);
		int safeHeight = height + 5;
		
		for (int x = -1; x <= 1; ++x)
			for (int z = -1; z <= 1; ++z)
				for (int y = 0; y < safeHeight; ++y)
					if (isLogBlock(world.getBlockAt(temp.set(at, x, y, z))))
						return false;
		
		world.setBlockAt(temp.set(at, 0, -1, 0), Blocks.DIRT.getDefaultState());
		
		for (int y = 0; y < height; y++)
			world.setBlockAt(temp.set(at, 0, y, 0), Blocks.LOG.getDefaultState());
		
		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				if (x != 0 || z != 0) {
					
					for (int y = height - 2; y < height; ++y) {
						
						if (world.getBlockAt(temp.set(at, x, y, z)) == null) {
							
							if ((x == 2 || x == -2) && (x == z || x == -z)) {
								if (y != height - 1 && rand.nextInt(2) == 0) {
									placeLeaves(world, temp);
								}
							} else {
								placeLeaves(world, temp);
							}
							
						}
						
					}
					
				}
			}
		}
		
		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				
				if (x == 0 || z == 0) {
					
					for (int y = height; y < height + 2; ++y) {
						
						if (world.getBlockAt(temp.set(at, x, y, z)) == null)
							placeLeaves(world, temp);
						
					}
					
				} else {
					
					if (rand.nextInt(2) == 0)
						if (world.getBlockAt(temp.set(at, x, height, z)) == null)
							placeLeaves(world, temp);
						
				}
				
			}
		}
		
		return true;
		
	}
	
	private static void placeLeaves(WorldServer world, BlockPositioned pos) {
		if (world.getBlockAt(pos) == null) world.setBlockAt(pos, Blocks.LEAVES.getDefaultState());
	}
	
	protected int getRandomHeight(Random rand) {
		return 4 + rand.nextInt(2);
	}
	
}
