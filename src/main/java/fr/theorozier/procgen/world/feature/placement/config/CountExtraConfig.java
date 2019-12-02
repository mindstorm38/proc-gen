package fr.theorozier.procgen.world.feature.placement.config;

public class CountExtraConfig extends CountConfig {
	
	private final int extraCount;
	private final float extraChance;
	
	public CountExtraConfig(int count, int extraCount, float extraChance) {
		
		super(count);
		
		this.extraCount = extraCount;
		this.extraChance = extraChance;
		
	}
	
	public int getExtraCount() {
		return this.extraCount;
	}
	
	public float getExtraChance() {
		return this.extraChance;
	}
	
}
