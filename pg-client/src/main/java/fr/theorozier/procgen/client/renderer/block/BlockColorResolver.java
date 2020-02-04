package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.common.world.biome.Biome;
import io.msengine.common.util.Color;

public interface BlockColorResolver {
	
	BlockColorResolver FOLIAGE_COLOR = Biome::getFoliageColor;
	BlockColorResolver GRASS_COLOR = Biome::getGrassColor;
	BlockColorResolver WATER_COLOR = Biome::getWaterColor;
	
	Color getColor(Biome biome);
	
}