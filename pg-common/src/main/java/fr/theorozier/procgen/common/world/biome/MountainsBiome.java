package fr.theorozier.procgen.common.world.biome;

public class MountainsBiome extends Biome {
	
	public MountainsBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.30f, 27f, Biomes.NO_SURFACE, Biomes.NO_SURFACE);
		
		addOres(this);
		
	}
	
}
