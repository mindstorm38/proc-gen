package fr.theorozier.procgen.common.world.task;

import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

public class WorldLoadingPosition extends SectionPosition {

	public static final int DEFAULT_LOADING_RADIUS = 8;
	public static final int MAX_LOADING_RADIUS = 32;
	
	private int loadingRadius = DEFAULT_LOADING_RADIUS;
	
	public WorldLoadingPosition() {
		super();
	}
	
	public WorldLoadingPosition(int x, int z) {
		super(x, z);
	}
	
	public WorldLoadingPosition(SectionPositioned sectionPos) {
		super(sectionPos);
	}
	
	public int getLoadingRadius() {
		return loadingRadius;
	}
	
	public void setLoadingRadius(int loadingRadius) {
		
		if (loadingRadius < 0 || loadingRadius > MAX_LOADING_RADIUS)
			throw new IllegalArgumentException("Invalid loading radius, must be in range 0.." + MAX_LOADING_RADIUS);
		
		this.loadingRadius = loadingRadius;
		
	}
	
}
