package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.Biomes;
import fr.theorozier.procgen.world.gen.WeatherBiomeProvider;

public class BetaBiomeProvider extends WeatherBiomeProvider {
	
	public BetaBiomeProvider(long seed) {
		super(seed);
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		
		// Temp : [-20; 50]
		// float temp = this.tempNoise.noise(x, z, 0.00001f) * 35f + 15f;
		
		// Humidity [0; 100]
		// float humidity = (this.humidityNoise.noise(x, z, 0.00001f) + 1f) * 50f;
		
		return Biomes.PLAINS;
		
	}
	
}
