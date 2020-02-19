package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import io.sutil.pool.FixedObjectPool;

import java.util.Random;

public class CactusFeature extends Feature<FeatureConfig> {
	
	private static boolean isSand(BlockState state) {
		return state != null && state.isBlock(Blocks.SAND);
	}
	
	@Override
	public boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, FeatureConfig config) {

		try (FixedObjectPool<BlockPosition>.PoolObject pos = BlockPosition.POOL.acquire()) {

			BlockPosition temp = pos.get();

			if (!isSand(world.getBlockAt(temp.set(at, 0, -1, 0))))
				return false;

			int height = 2 + rand.nextInt(2);

			for (int y = 0; y < height; ++y)
				for (int x = -1; x <= 1; ++x)
					for (int z = -1; z <= 1; ++z)
						if (x == 0 || z == 0)
							if (world.getBlockAt(temp.set(at, x, y, z)) != null)
								return false;

			if (world.getBlockAt(temp.set(at, 0, height, 0)) != null)
				return false;

			for (int y = 0; y < height; ++y)
				if (world.getBlockAt(temp.set(at, 0, y, 0)) == null)
					world.setBlockAt(temp, Blocks.CACTUS.getDefaultState());

			return true;

		}
		
	}
	
}
