package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.gen.biome.WeatherBiomeProvider;

import static fr.theorozier.procgen.common.world.biome.Biomes.*;

public class BetaBiomeProvider extends WeatherBiomeProvider {
	
	public BetaBiomeProvider(long seed) {
		
		super(seed);
		
		//float[] tempRanges = {0.25f, 0.50f, 0.75f};
		//float[] humidityRanges = {0.25f, 0.50f, 0.75f};
		
		/*Biome[][] biomesMap = {
				{ Biomes.MOUNTAINS, Biomes.LOW_HILL, Biomes.SAVANNA, Biomes.DESERT },
				{ Biomes.TAIGA, Biomes.PLAIN, Biomes.PLAIN, Biomes.SAVANNA },
				{ Biomes.RIVER, Biomes.FOREST, Biomes.FOREST, Biomes.JUNGLE },
				{ Biomes.RIVER, Biomes.RIVER, Biomes.JUNGLE, Biomes.JUNGLE }
		};*/
		
		/*
		Biome[][] biomesMap = {
				{ Biomes.MOUNTAINS, Biomes.LOW_HILL, Biomes.RIVER, Biomes.DESERT },
				{ Biomes.LOW_HILL, Biomes.RIVER, Biomes.PLAIN, Biomes.SAVANNA },
				{ Biomes.RIVER, Biomes.FOREST, Biomes.FOREST, Biomes.FOREST },
				{ Biomes.RIVER, Biomes.PLAIN, Biomes.FOREST, Biomes.FOREST }
		};
		*/
		
		float[] tempRanges = {0.125f, 0.250f, 0.375f, 0.500f, 0.625f, 0.750f, 0.875f};
		float[] humidityRanges = {0.125f, 0.250f, 0.375f, 0.500f, 0.625f, 0.750f, 0.875f};
		
		Biome[][] biomesMap = {
				{ TUNDRA, TUNDRA, TUNDRA, RIVER,  MOUNTAINS, RIVER,    SAVANNA,  DESERT       },
				{ OCEAN,  TAIGA,  RIVER,  TUNDRA, MOUNTAINS, RIVER,    SAVANNA,  DESERT_HILL  },
				{ OCEAN,  OCEAN,  RIVER,  FOREST, FOREST,    RIVER,    SAVANNA,  SAVANNA      },
				{ OCEAN,  OCEAN,  OCEAN,  FOREST, FOREST,    RIVER,    PLAIN,    SAVANNA      },
				{ OCEAN,  OCEAN,  OCEAN,  OCEAN,  RIVER,     PLAIN,    PLAIN,    LOW_HILL     },
				{ OCEAN,  OCEAN,  OCEAN,  OCEAN,  OCEAN,     LOW_HILL, RIVER,    RIVER        },
				{ OCEAN,  OCEAN,  OCEAN,  OCEAN,  OCEAN,     OCEAN,    JUNGLE,   JUNGLE       },
				{ OCEAN,  OCEAN,  OCEAN,  OCEAN,  OCEAN,     OCEAN,    OCEAN,    JUNGLE       }
		};
		
		this.setWeatherMap(tempRanges, humidityRanges, biomesMap);
		
	}
	
}
