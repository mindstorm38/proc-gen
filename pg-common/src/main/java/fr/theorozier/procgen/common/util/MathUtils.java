package fr.theorozier.procgen.common.util;

import io.sutil.math.MathHelper;

public class MathUtils {
	
	public static boolean isPowerOfTwo(int n) {
		return n > 0 && (n & (n - 1)) == 0;
	}
	
	public static float lerp(float n1, float n2, float r) {
		return MathHelper.interpolate(r, n2, n1);
	}
	
	/**
	 * Function used to throw an {@link IllegalArgumentException} if a number is not in specified range.
	 * @param n The number to check.
	 * @param min Minimum value for the range.
	 * @param max Maximum value for the range.
	 * @param property Property name to show in the error message, of the pattern "&lt;property&gt; must be between &lt;min&gt; and &lt;max&gt;.".
	 * @return The given number if in range.
	 */
	public static int requireIntegerInRange(int n, int min, int max, String property) {
		
		if (n < min || n > max)
			throw new IllegalArgumentException(property + " must be between " + min + " and " + max + '.');
		
		return n;
		
	}

}
