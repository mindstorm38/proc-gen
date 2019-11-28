package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.biome.Biome;

public class ChunkWeather {
	
	private final float temp;
	private final float humidity;
	private final Biome biome;
	
	public ChunkWeather(float temp, float humidity, Biome biome) {
		
		this.temp = temp;
		this.humidity = humidity;
		this.biome = biome;
		
	}
	
	public float getTemp() {
		return this.temp;
	}
	
	public float getHumidity() {
		return this.humidity;
	}
	
	public Biome getBiome() {
		return this.biome;
	}
	
}
