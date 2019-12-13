package fr.theorozier.procgen.world.biome;

public class JungleBiome extends Biome {
	
	public JungleBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(57, 183, 23);
		this.getGrassColor().setAll(57, 183, 23);
		
		addPlantGrass(this);
		
	}
	
}
