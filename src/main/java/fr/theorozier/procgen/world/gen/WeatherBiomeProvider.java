package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.Biomes;
import io.msengine.common.util.noise.OctaveSimplexNoise;

import java.util.*;

public class WeatherBiomeProvider extends BiomeProvider {
	
	private final OctaveSimplexNoise tempNoise;
	private final OctaveSimplexNoise humidityNoise;
	private final OctaveSimplexNoise saltNoise;
	
	private final Map<Biome, Byte> biomes;
	
	public WeatherBiomeProvider(long seed) {
		
		super(seed);
		
		this.tempNoise = new OctaveSimplexNoise(getTempSeed(seed), 4, 0.5f, 2f);
		this.humidityNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.5f, 2f);
		this.saltNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.5f, 2f);
		
		this.biomes = new HashMap<>();
		
	}
	
	protected void addBiome(Biome biome, int priority) {
		this.biomes.put(biome, (byte) priority);
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		
		// Temp : [-20; 50]
		// float temp = this.tempNoise.noise(x, z, 0.00001f) * 35f + 15f;
		
		// Humidity [0; 100]
		// float humidity = (this.humidityNoise.noise(x, z, 0.00001f) + 1f) * 50f;
		
		if (this.humidityNoise.noise(x, z, 0.01f) > 0) {
			return Biomes.FOREST_HILL;
		} else {
			return Biomes.PLAIN;
		}
		
	}
	
	public static long getTempSeed(long seed) {
		return seed * 45616062682809L + 75864190373326L;
	}
	
	public static long getHumiditySeed(long seed) {
		return seed * 96503618537404L + 65361195238780L;
	}
	
}
