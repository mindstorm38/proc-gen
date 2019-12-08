package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.PlantFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.ChanceCountConfig;

public class DesertHillBiome extends Biome {
	
	private static final BiomeWeatherRange WEATHER = new BiomeWeatherRange(45f, 50f, 0f, 10f);
	
	public DesertHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.30f, 24f, WEATHER, Biomes.DESERT_SURFACE);
		
		addOres(this);
		
		this.addPlacedFeature(
				Placements.SURFACE_CHANCE_MULTIPLE,
				new ChanceCountConfig(10, 0.3f),
				Features.PLANT,
				new PlantFeatureConfig(Blocks.PLANT_DEADBUSH, block -> block == Blocks.GRASS)
		);
		
	}
	
}
