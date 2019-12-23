package fr.theorozier.procgen.common.world.feature.placement.config;

public class UndergroundConfig implements PlacementConfig {

	private final int bottomOffset;
	private final int topOffset;
	
	private final int chanceCount;
	private final float chance;
	
	public UndergroundConfig(int bottomOffset, int topOffset, int chanceCount, float chance) {
		
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		
		this.chanceCount = chanceCount;
		this.chance = chance;
		
	}
	
	public int getBottomOffset() {
		return this.bottomOffset;
	}
	
	public int getTopOffset() {
		return this.topOffset;
	}
	
	public int getChanceCount() {
		return this.chanceCount;
	}
	
	public float getChance() {
		return this.chance;
	}

}
