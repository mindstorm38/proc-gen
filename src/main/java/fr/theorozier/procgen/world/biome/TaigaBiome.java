package fr.theorozier.procgen.world.biome;

public class TaigaBiome extends Biome {
	
	public TaigaBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 12f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(110 / 255f, 152 / 255f, 143 / 255f);
		
	}
	
}
