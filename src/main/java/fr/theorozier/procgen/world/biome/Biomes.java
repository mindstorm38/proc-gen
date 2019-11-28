package fr.theorozier.procgen.world.biome;

import java.util.HashMap;
import java.util.Map;

public class Biomes {

	private static final Map<Short, Biome> uidRegister = new HashMap<>();

	public static final PlainsBiome PLAINS = registerBiome(new PlainsBiome(1, "plains"));
	
	public static <B extends Biome> B registerBiome(B biome) {
	
		uidRegister.put(biome.getUid(), biome);
		
		return biome;
	
	}
	
}
