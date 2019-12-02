package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.world.biome.Biomes;
import fr.theorozier.procgen.world.gen.WeatherBiomeProvider;

public class BetaBiomeProvider extends WeatherBiomeProvider {
	
	public BetaBiomeProvider(long seed) {
		
		super(seed);
		
		this.addBiome(Biomes.PLAIN, 1);
		this.addBiome(Biomes.FOREST, 2);
		this.addBiome(Biomes.LOW_HILL, 3);
		this.addBiome(Biomes.DESERT, 4);
		this.addBiome(Biomes.DESERT_HILL, 5);
		
	}
	
}
