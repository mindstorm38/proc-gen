package fr.theorozier.procgen.world.feature.placement.config;

public class ChanceCountConfig extends ChanceConfig {
	
	private int count;
	
	public ChanceCountConfig(int count, float chance) {
		
		super(chance);
		this.count = count;
		
	}
	
	public int getCount() {
		return this.count;
	}
	
}
