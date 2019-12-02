package fr.theorozier.procgen.world.feature.placement.config;

public class CountConfig implements PlacementConfig {
	
	private final int count;
	
	public CountConfig(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
	
}
