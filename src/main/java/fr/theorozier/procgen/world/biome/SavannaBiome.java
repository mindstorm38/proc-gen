package fr.theorozier.procgen.world.biome;

public class SavannaBiome extends Biome {
	
	public SavannaBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(164 / 255f, 164 / 255f, 52 / 255f);
		
	}
	
}
