package fr.theorozier.procgen.common.world.biome;

public class TaigaBiome extends Biome {
	
	public TaigaBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.28f, 12f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(103, 164, 103);
		this.getGrassColor().setAll(103, 164, 103);
		this.getWaterColor().setAll(COLD_WATER_COLOR);
		
		addOres(this);
		addPlantGrass(this);
		
	}
	
}
