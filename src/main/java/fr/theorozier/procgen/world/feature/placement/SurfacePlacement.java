package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.Heightmap;
import fr.theorozier.procgen.world.feature.placement.config.PlacementConfig;

import java.util.Random;

public abstract class SurfacePlacement<C extends PlacementConfig> extends Placement<C> {

	protected static BlockPosition randomSurfacePosition(World world, Random rand, BlockPosition origin) {
		
		int xRand = rand.nextInt(16);
		int zRand = rand.nextInt(16);
		return world.getBlockHeightAt(Heightmap.Type.WORLD_SURFACE, origin.add(xRand, 0, zRand));
		
	}

}
