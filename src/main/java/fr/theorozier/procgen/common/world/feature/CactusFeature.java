package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;

public class CactusFeature extends Feature<FeatureConfig> {
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, FeatureConfig config) {
		
		if (world.getBlockTypeAt(at.getX(), at.getY() - 1, at.getZ()) != Blocks.SAND)
			return false;
		
		int maxY = at.getY() + 3;
		
		WorldBlock block;
		
		for (int y = at.getY(); y < maxY; ++y)
			for (int x = -1; x <= 1; ++x)
				for (int z = -1; z <= 1; ++z)
					if (x == 0 || z == 0)
						if ((block = world.getBlockAt(at.getX() + x, y, at.getZ() + z)) != null && block.isSet())
							return false;
		
		if ((block = world.getBlockAt(at.getX(), maxY, at.getZ())) != null && block.isSet())
			return false;
		
		for (int y = at.getY(); y < maxY; ++y)
			if ((block = world.getBlockAt(at.getX(), y, at.getZ())) != null)
				block.setBlockType(Blocks.CACTUS);
		
		return true;
		
	}
	
}
