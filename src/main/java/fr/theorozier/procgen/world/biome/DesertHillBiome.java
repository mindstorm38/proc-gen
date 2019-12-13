package fr.theorozier.procgen.world.biome;

public class DesertHillBiome extends Biome {
	
	public DesertHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.28f, 14f, Biomes.DESERT_SURFACE, Biomes.UNDERWATER_SAND_SURFACE);
		
		this.getFoliageColor().setAll(DesertBiome.FOLIAGE_COLOR);
		this.getGrassColor().setAll(DesertBiome.GRASS_COLOR);
		this.getWaterColor().setAll(WARM_WATER_COLOR);
		
		addOres(this);
		addDeadBushes(this);
		
	}
	
}
