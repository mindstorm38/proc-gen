package fr.theorozier.procgen.world.biome;

public class SavannaBiome extends Biome {
	
	public SavannaBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_SAND_SURFACE);
		
		this.getFoliageColor().setAll(DesertBiome.FOLIAGE_COLOR);
		this.getGrassColor().setAll(DesertBiome.GRASS_COLOR);
		this.getWaterColor().setAll(WARM_WATER_COLOR);
		
		addOres(this);
		addPlantGrass(this);
		
	}
	
}
