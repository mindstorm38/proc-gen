package fr.theorozier.procgen.world.feature;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.WorldBlock;
import fr.theorozier.procgen.world.WorldBlockPosition;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;

public class TreeFeature extends Feature<NoFeatureConfig> {
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, WorldBlockPosition at, NoFeatureConfig config) {
		
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
