package fr.theorozier.procgen.common.world.util;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import io.sutil.math.MathHelper;

import java.util.Random;

public class ExplosionCreator {

	public static void createExplosion(WorldServer world, float x, float y, float z, float size) {
	
		Random random = world.getRandom();
		
		final int baseRange = MathHelper.floorFloatInt(size);
		final int rangeSq = baseRange * baseRange;
		
		int cx = MathHelper.floorDoubleInt(x);
		int cy = MathHelper.floorDoubleInt(y);
		int cz = MathHelper.floorDoubleInt(z);
		
		BlockPosition pos = new BlockPosition();
		BlockState airState = Blocks.AIR.getDefaultState();
		
		for (int dx = -baseRange; dx <= baseRange; ++dx) {
			for (int dz = -baseRange; dz <= baseRange; ++dz) {
				for (int dy = -baseRange; dy <= baseRange; ++dy) {
					
					if ((dx * dx + dy * dy + dz * dz) > (rangeSq - (2 * random.nextFloat())))
						continue;
					
					pos.set(cx + dx, cy + dy, cz + dz);
					world.setBlockAt(pos, airState);
					
					world.getEventManager().fireListeners(WorldChunkListener.class,
							l -> l.worldChunkBlockChanged(world, world.getChunkAtBlock(pos), pos, airState));
					
				}
			}
		}
		
	}
	
}
