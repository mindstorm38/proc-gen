package fr.theorozier.procgen.common.world.biome;

public class TundraBiome extends Biome {
	
	public TundraBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 6f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.getFoliageColor().setAll(103, 164, 103);
		this.getGrassColor().setAll(103, 164, 103);
		this.getWaterColor().setAll(COLD_WATER_COLOR);
		
		addOres(this);
		addPlantGrass(this);
		
	}
	
}
