package fr.theorozier.procgen.world.feature;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public class TreeFeature extends Feature<FeatureConfig> {
	
	private static boolean canGrowOn(Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS;
	}
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, FeatureConfig config) {
		
		if (!canGrowOn(world.getBlockTypeAt(at.getX(), at.getY() - 1, at.getZ())))
			return false;
		
		int height = this.getRandomHeight(rand);
		int safeHeight = height + 5;
		
		WorldBlock block;
		
		for (int x = -1; x <= 1; ++x)
			for (int z = -1; z <= 1; ++z)
				for (int y = 0; y < safeHeight; ++y)
					if (world.getBlockTypeAt(at.add(x, y, z)) == Blocks.LOG)
						return false;
		
		for (int y = 0; y < height; y++)
			if ((block = world.getBlockAt(at.add(0, y, 0))) != null)
				block.setBlockType(Blocks.LOG);
		
		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				if (x != 0 || z != 0) {
					
					for (int y = height - 2; y < height; ++y) {
						
						if ((block = world.getBlockAt(at.add(x, y, z))) != null) {
							
							
							if ((x == 2 || x == -2) && (x == z || x == -z)) {
								if (y != height - 1 && rand.nextInt(2) == 0) {
									placeLeaves(block);
								}
							} else {
								placeLeaves(block);
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
						
						if ((block = world.getBlockAt(at.add(x, y, z))) != null)
							placeLeaves(block);
						
					}
					
				} else {
					
					if (rand.nextInt(2) == 0)
						if ((block = world.getBlockAt(at.add(x, height, z))) != null)
							placeLeaves(block);
						
				}
				
			}
		}
		
		return true;
		
	}
	
	private static void placeLeaves(WorldBlock block) {
		if (!block.isSet()) block.setBlockType(Blocks.LEAVES);
	}
	
	protected int getRandomHeight(Random rand) {
		return 4 + rand.nextInt(2);
	}
	
}
