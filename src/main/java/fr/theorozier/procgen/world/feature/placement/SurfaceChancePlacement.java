package fr.theorozier.procgen.world.feature.placement;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.feature.placement.config.ChanceConfig;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.Random;
import java.util.stream.Stream;

public class SurfaceChancePlacement extends Placement<ChanceConfig> {
	
	@Override
	protected Stream<BlockPosition> position(World world, ChunkGenerator generator, Random rand, BlockPosition at, ChanceConfig config) {
		
		return null;
		
	}
	
}
