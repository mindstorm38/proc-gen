package fr.theorozier.procgen.world.feature.placement.config;

public class SurfaceCountConfig implements PlacementConfig {
	
	private final int count;
	
	public SurfaceCountConfig(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
	
}
