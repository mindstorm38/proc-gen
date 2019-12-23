package fr.theorozier.procgen.common.world.feature;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.common.world.feature.config.PlantFeatureConfig;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.Random;

public class PlantFeature extends Feature<PlantFeatureConfig> {
	
	@Override
	public boolean place(World world, ChunkGenerator generator, Random rand, BlockPosition at, PlantFeatureConfig config) {
		
		if (!config.canPlaceOn(world.getBlockTypeAt(at.getX(), at.getY() - 1, at.getZ())))
			return false;
		
		WorldBlock wb = world.getBlockAt(at);
		
		if (wb == null || wb.isSet())
			return false;
		
		wb.setBlockType(config.getPlantBlock());
		
		return true;
		
	}
	
}
