package fr.theorozier.procgen.world.biome;

import io.msengine.common.util.Color;

public class DesertBiome extends Biome {
	
	public static final Color FOLIAGE_COLOR = new Color(164, 164, 52);
	public static final Color GRASS_COLOR = new Color(164, 164, 52);
	
	public DesertBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.25f, 8f, Biomes.DESERT_SURFACE, Biomes.UNDERWATER_SAND_SURFACE);
		
		this.getFoliageColor().setAll(FOLIAGE_COLOR);
		this.getGrassColor().setAll(GRASS_COLOR);
		
		addOres(this);
		addDeadBushes(this);
		
	}
	
}
