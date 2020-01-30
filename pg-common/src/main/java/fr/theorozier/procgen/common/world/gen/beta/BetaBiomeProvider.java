package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.biome.Biomes;
import fr.theorozier.procgen.common.world.gen.biome.WeatherBiomeProvider;

public class BetaBiomeProvider extends WeatherBiomeProvider {
	
	public BetaBiomeProvider(long seed) {
		
		super(seed);
		
		float[] tempRanges = {0.25f, 0.50f, 0.75f};
		float[] humidityRanges = {0.25f, 0.50f, 0.75f};
		
		/*Biome[][] biomesMap = {
				{ Biomes.MOUNTAINS, Biomes.LOW_HILL, Biomes.SAVANNA, Biomes.DESERT },
				{ Biomes.TAIGA, Biomes.PLAIN, Biomes.PLAIN, Biomes.SAVANNA },
				{ Biomes.RIVER, Biomes.FOREST, Biomes.FOREST, Biomes.JUNGLE },
				{ Biomes.RIVER, Biomes.RIVER, Biomes.JUNGLE, Biomes.JUNGLE }
		};*/
		
		Biome[][] biomesMap = {
				{ Biomes.MOUNTAINS, Biomes.LOW_HILL, Biomes.RIVER, Biomes.DESERT },
				{ Biomes.LOW_HILL, Biomes.RIVER, Biomes.PLAIN, Biomes.SAVANNA },
				{ Biomes.RIVER, Biomes.FOREST, Biomes.FOREST, Biomes.FOREST },
				{ Biomes.RIVER, Biomes.PLAIN, Biomes.FOREST, Biomes.FOREST }
		};
		
		this.setWeatherMap(tempRanges, humidityRanges, biomesMap);
		
	}
	
}
