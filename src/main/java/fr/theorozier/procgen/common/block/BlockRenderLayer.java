package fr.theorozier.procgen.common.block;

public enum BlockRenderLayer {
	
	OPAQUE,
	CUTOUT,
	CUTOUT_NOT_CULLED,
	TRANSPARENT;
	
	public static final int COUNT = values().length;
	
}
