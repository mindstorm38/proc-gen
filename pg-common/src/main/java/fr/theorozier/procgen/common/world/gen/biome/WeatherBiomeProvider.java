package fr.theorozier.procgen.common.world.gen.biome;

import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.biome.Biomes;
import io.msengine.common.util.noise.OctaveSimplexNoise;

public class WeatherBiomeProvider extends BiomeProvider {
	
	private final OctaveSimplexNoise tempNoise;
	private final OctaveSimplexNoise humidityNoise;
	private final OctaveSimplexNoise saltNoise;
	
	private Biome[][] map;
	private float[] tempRanges;
	private float[] humidityRanges;
	
	public WeatherBiomeProvider(long seed) {
		
		super(seed);
		
		this.tempNoise = new OctaveSimplexNoise(getTempSeed(seed), 4, 0.4f, 2.5f);
		this.humidityNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.4f, 2.5f);
		this.saltNoise = new OctaveSimplexNoise(getHumiditySeed(seed), 4, 0.4f, 2f);
		
		this.setWeatherMap(new float[0], new float[0], new Biome[][]{{Biomes.EMPTY}});
		
	}
	
	protected void setWeatherMap(float[] tempRanges, float[] humidityRanges, Biome[][] map) {
		
		if (map.length == 0)
			throw new IllegalArgumentException("Invalid heat/humidity map, can't have no row.");
		
		if (map.length != (humidityRanges.length + 1))
			throw new IllegalArgumentException("Inconsistent humidity ranges length.");
		
		int rowLength = map[0].length;
		
		for (int r = 1; r < map.length; ++r)
			if (rowLength != map[r].length)
				throw new IllegalArgumentException("Inconsistent humidity row length.");
			
		if (rowLength == 0)
			throw new IllegalArgumentException("Invalid heat/humidity map, can't have no column.");
			
		if (rowLength != (tempRanges.length + 1))
			throw new IllegalArgumentException("Inconsistent temperature ranges length.");
	
		this.map = map;
		this.tempRanges = tempRanges;
		this.humidityRanges = humidityRanges;
		
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		
		// Temp : [0; 1]
		float temp = (this.tempNoise.noise(x, z, 0.001f) + 1f) / 2f;
		
		// Humidity [0; 1]
		float humidity = (this.humidityNoise.noise(x, z, 0.001f) + 1f) / 2f;
		
		int tl = this.tempRanges.length;
		int hl = this.humidityRanges.length;
		
		Biome biome = null;
		
		for (int ti = 0; ti <= tl; ++ti) {
			if (ti == tl || temp < this.tempRanges[ti]) {
				
				for (int hi = 0; hi <= hl; ++hi) {
					if (hi == hl || humidity < this.humidityRanges[hi]) {
						
						biome = this.map[hi][ti];
						break;
						
					}
				}
				
				break;
				
			}
		}
		
		return biome == null ? Biomes.EMPTY : biome;
		
	}
	
	public static long getTempSeed(long seed) {
		return seed * 45616062682809L + 75864190373326L;
	}
	
	public static long getHumiditySeed(long seed) {
		return seed * 96503618537404L + 65361195238780L;
	}
	
}
