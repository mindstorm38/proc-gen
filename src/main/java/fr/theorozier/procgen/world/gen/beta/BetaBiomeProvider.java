package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.world.biome.Biomes;
import fr.theorozier.procgen.world.gen.WeatherBiomeProvider;

public class BetaBiomeProvider extends WeatherBiomeProvider {
	
	public BetaBiomeProvider(long seed) {
		
		super(seed);
		
		this.addBiome(Biomes.PLAIN, 1);
		this.addBiome(Biomes.FOREST_HILL, 2);
		
	}
	
}
