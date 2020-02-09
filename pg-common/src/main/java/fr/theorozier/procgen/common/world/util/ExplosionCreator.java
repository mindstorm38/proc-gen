package fr.theorozier.procgen.common.world.util;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.MotionEntity;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
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
		
		BlockState airState = Blocks.AIR.getDefaultState();
		
		for (int dx = -baseRange; dx <= baseRange; ++dx) {
			for (int dz = -baseRange; dz <= baseRange; ++dz) {
				for (int dy = -baseRange; dy <= baseRange; ++dy) {
					
					if ((dx * dx + dy * dy + dz * dz) > (rangeSq - (2 * random.nextFloat())))
						continue;
					
					world.setBlockAt(cx + dx, cy + dy, cz + dz, airState);
					
				}
			}
		}
		
		AxisAlignedBB explosionBox = new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);
		
		world.forEachEntitiesIn(explosionBox, e -> {
		
			if (e instanceof MotionEntity) {
				
				MotionEntity entity = (MotionEntity) e;
				
				double dx = entity.getPosX() - x;
				double dy = entity.getPosY() - y;
				double dz = entity.getPosZ() - z;
				double sqDist = dx * dx + dy * dy + dz * dz;
				
				if (sqDist <= rangeSq) {
					
					double distRatio = 1 - (sqDist / rangeSq);
					entity.addVelocity(dx * distRatio, dy * distRatio, dz * distRatio);
					
				}
				
			}
		
		}, true);
		
	}
	
}
