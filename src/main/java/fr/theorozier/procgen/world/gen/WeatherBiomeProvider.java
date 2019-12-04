package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.Biomes;
import io.msengine.common.util.noise.OctaveSimplexNoise;

import java.util.*;
import java.util.stream.Collectors;

public class WeatherBiomeProvider extends BiomeProvider {
	
	private final OctaveSimplexNoise tempNoise;
	private final OctaveSimplexNoise humidityNoise;
	private final OctaveSimplexNoise saltNoise;
	
	private final Map<Biome, Byte> biomes;
	private final List<Biome> biomesList;
	
	public WeatherBiomeProvider(long seed) {
		
		super(seed);
		
		this.tempNoise = new OctaveSimplexNoise(getTempSeed(seed), 4, 0.2f, 0.5f);
		this.humidityNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.2f, 0.5f);
		this.saltNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.2f, 0.5f);
		
		this.biomes = new HashMap<>();
		this.biomesList = new ArrayList<>();
		
	}
	
	protected void addBiome(Biome biome, int priority) {
		
		this.biomes.put(biome, (byte) priority);
		this.biomesList.add(biome);
		
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		
		if (this.biomes.isEmpty()) {
			
			return Biomes.EMPTY;
			
		} else {
			
			// Temp : [-30; 60]
			float temp = this.tempNoise.noise(x, z, 0.00005f) * 45f + 15f;
			
			// Humidity [-20; 120]
			float humidity = this.humidityNoise.noise(x, z, 0.00005f) * 70f + 50f;
			
			List<Biome> biomes = this.biomesList.stream()
					.filter(b -> b.getWeather().isInRange(temp, humidity))
					.collect(Collectors.toList());
			
			if (biomes.isEmpty())
				biomes = this.biomesList;
			
			return biomes.get((int) ((this.saltNoise.noise(x, z, 0.0001f) + 1f) / 2f * biomes.size()));
			
		}
		
	}
	
	public static long getTempSeed(long seed) {
		return seed * 45616062682809L + 75864190373326L;
	}
	
	public static long getHumiditySeed(long seed) {
		return seed * 96503618537404L + 65361195238780L;
	}
	
}
