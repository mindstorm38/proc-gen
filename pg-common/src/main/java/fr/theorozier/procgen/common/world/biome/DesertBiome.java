package fr.theorozier.procgen.common.world.biome;

import io.msengine.common.util.Color;

public class DesertBiome extends Biome {
	
	public static final Color FOLIAGE_COLOR = new Color(198, 212, 112);
	public static final Color GRASS_COLOR = new Color(198, 212, 112);
	
	public DesertBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 8f, Biomes.DESERT_SURFACE, Biomes.UNDERWATER_SAND_SURFACE);
		
		this.getFoliageColor().setAll(FOLIAGE_COLOR);
		this.getGrassColor().setAll(GRASS_COLOR);
		//this.getWaterColor().setAll(WARM_WATER_COLOR);
		
		addOres(this);
		addDeadBushes(this);
		addCactus(this);
		
	}
	
}
