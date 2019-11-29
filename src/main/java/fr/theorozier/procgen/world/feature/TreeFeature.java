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
		
		if (!canGrowOn(world.getBlockTypeAt(at.getX(), at.getY(), at.getZ())))
			return false;
		
		int height = this.getRandomHeight(rand);
		
		WorldBlock block;
		
		for (int y = 0; y < height; y++) {
			block = world.getBlockAt(at.add(0, y, 0));
			if (block != null) {
				block.setBlockType(Blocks.LOG);
			}
		}
		
		return true;
		
	}
	
	protected int getRandomHeight(Random rand) {
		return 4 + rand.nextInt(3);
	}
	
}
