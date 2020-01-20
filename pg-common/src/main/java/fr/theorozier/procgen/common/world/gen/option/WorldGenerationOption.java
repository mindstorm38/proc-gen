package fr.theorozier.procgen.common.world.gen.option;

import io.sutil.StringUtils;

import java.util.UUID;

public class WorldGenerationOption {
	
	private final long seed;
	
	public WorldGenerationOption(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public static long getSeedFromString(String str) {
		return UUID.nameUUIDFromBytes(str.getBytes(StringUtils.CHARSET_US_ASCII)).getMostSignificantBits();
	}
	
}
