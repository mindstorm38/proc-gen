package fr.theorozier.procgen.common.util;

import io.sutil.math.MathHelper;

public class MathUtils {
	
	public static boolean isPowerOfTwo(int n) {
		return n > 0 && (n & (n - 1)) == 0;
	}
	
	public static float lerp(float n1, float n2, float r) {
		return MathHelper.interpolate(r, n2, n1);
	}

}
