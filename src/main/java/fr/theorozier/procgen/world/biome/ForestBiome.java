package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.config.PlantFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.ChanceCountConfig;
import fr.theorozier.procgen.world.feature.placement.config.CountExtraConfig;

public class ForestBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(10f, 15f, 30f, 100f);
	
	public ForestBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.29f, 12f, WEATHER, Biomes.GRASS_SURFACE);
		
		addNormalForest(this);
		addBasicFlowers(this);
		addOres(this);
		
	}
	
}
