package fr.theorozier.procgen.util;

public class MathUtils {
	
	/**
	 * Linear interpolation method.
	 * @return A linear interpolation of n1 ==> n2 by w.
	 */
	public static float lerp(float n1, float n2, float w) {
		return (1.0f - w) * n1 + w * n2;
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

}
