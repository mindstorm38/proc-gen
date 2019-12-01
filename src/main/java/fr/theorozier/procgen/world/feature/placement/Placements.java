package fr.theorozier.procgen.world.feature.placement;

public class Placements {
	
	private Placements() {}
	
	public static final SurfaceChancePlacement                  SURFACE_CHANCE = new SurfaceChancePlacement();
	public static final SurfaceChanceMultiplePlacement SURFACE_CHANCE_MULTIPLE = new SurfaceChanceMultiplePlacement();
	public static final SurfaceCountPlacement                    SURFACE_COUNT = new SurfaceCountPlacement();
	public static final SurfaceCountExtraPlacement         SURFACE_COUNT_EXTRA = new SurfaceCountExtraPlacement();
	
}
