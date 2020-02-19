package fr.theorozier.procgen.common.world.feature.placement;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import io.sutil.pool.FixedObjectPool;

import java.util.Random;

public abstract class SurfacePlacement<C extends PlacementConfig> extends Placement<C> {

	private static final SectionPosition cachedSectionPos = new SectionPosition();
	
	protected static ImmutableBlockPosition randomSurfacePosition(WorldAccessorServer world, Random rand, BlockPositioned origin) {
		
		int xRand = rand.nextInt(16);
		int zRand = rand.nextInt(16);
		
		try (FixedObjectPool<SectionPosition>.PoolObject temp = SectionPosition.POOL.acquire()) {
			
			return world.getBlockHeightAt(
					Heightmap.Type.WORLD_SURFACE,
					temp.get().set(origin.getX() + xRand, origin.getZ() + zRand)
			);
			
		}
		
	}

}
