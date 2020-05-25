package fr.theorozier.procgen.common.block;

public enum BlockRenderLayer {
	
	OPAQUE,
	CUTOUT,
	TRANSPARENT;
	
	public static final int COUNT = values().length;
	
}
