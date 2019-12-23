package fr.theorozier.procgen.common.world.feature.placement.config;

public class ChanceConfig implements PlacementConfig {
	
	private final float chance;
	
	public ChanceConfig(float chance) {
		this.chance = chance;
	}
	
	public float getChance() {
		return this.chance;
	}
	
}
