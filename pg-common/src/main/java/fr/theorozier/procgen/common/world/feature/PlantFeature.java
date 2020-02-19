package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.feature.config.PlantFeatureConfig;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.Random;

public class PlantFeature extends Feature<PlantFeatureConfig> {
	
	@Override
	public boolean place(WorldAccessorServer world, ChunkGenerator generator, Random rand, AbsBlockPosition at, PlantFeatureConfig config) {
		
		if (!config.canPlaceOn(world.getBlockAt(at.getX(), at.getY() - 1, at.getZ())))
			return false;
		
		BlockState state = world.getBlockAt(at);
		
		if (state != null)
			return false;
		
		world.setBlockAt(at, config.getPlantBlock());
		
		return true;
		
	}
	
}
