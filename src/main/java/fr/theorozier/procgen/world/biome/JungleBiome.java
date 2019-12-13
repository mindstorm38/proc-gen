package fr.theorozier.procgen.world.biome;

public class JungleBiome extends Biome {
	
	public JungleBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(38 / 255f, 162 / 255f, 73 / 255f);
		
	}
	
}
