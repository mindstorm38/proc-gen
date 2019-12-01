package fr.theorozier.procgen.world.biome;

import java.util.HashMap;
import java.util.Map;

public class Biomes {

	private static final Map<Short, Biome> uidRegister = new HashMap<>();

	public static final EmptyBiome EMPTY = registerBiome(new EmptyBiome(1, "empty"));
	public static final PlainBiome PLAIN = registerBiome(new PlainBiome(2, "plain"));
	public static final ForestHillBiome FOREST_HILL = registerBiome(new ForestHillBiome(3, "forest_hill"));
	public static final LowHillBiome LOW_HILL = registerBiome(new LowHillBiome(4, "low_hill"));
	
	public static <B extends Biome> B registerBiome(B biome) {
	
		uidRegister.put(biome.getUid(), biome);
		
		return biome;
	
	}
	
}
