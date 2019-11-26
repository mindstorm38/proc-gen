package fr.theorozier.procgen.util;

import org.joml.SimplexNoise;

public class MathUtils {
	
	/**
	 * Linear interpolation method.
	 * @return A linear interpolation of n1 ==> n2 by w.
	 */
	public static float lerp(float n1, float n2, float w) {
		return (1.0f - w) * n1 + w * n2;
	}
	
	public static float invlerp(float n1, float n2, float value) {
		return (value - n1) / (n2 - n1);
	}
	
	/**
	 * Fast floor util.
	 * @param v Value to be rounded off.
	 * @return Floor value of x.
	 */
	public static int fastfloor(float v) {
		
		final int i = (int) v;
		return v < i ? i - 1 : i;
		
	}
	
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
