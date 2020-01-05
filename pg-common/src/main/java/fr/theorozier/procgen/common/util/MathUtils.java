package fr.theorozier.procgen.common.util;

import org.joml.SimplexNoise;

public class MathUtils {
	
	/**
	 * Clamp the JOML simplex noise between 0 and 1;
	 * @see SimplexNoise#noise(float, float)
	 */
	public static float simplex(float x, float y) {
		return (SimplexNoise.noise(x, y) + 1f) / 2f;
	}
	
	public static float simplex(float x, float y, float z) {
		return (SimplexNoise.noise(x, y, z) + 1f) / 2f;
	}
	
	public static float simplexOctaves(float x, float y, int octaves, float persistance, float lacunarity) {
	
		float ampl = 1f, freq = 1f, noise = 0f;
		
		for (int o = 0; o < octaves; o++) {
		
			noise += simplex(x * freq, y * freq) * ampl;
			freq *= persistance;
			ampl *= lacunarity;
			
		}
		
		return noise;
	
	}

}
