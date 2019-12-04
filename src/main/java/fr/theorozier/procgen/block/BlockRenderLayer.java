package fr.theorozier.procgen.block;

public enum BlockRenderLayer {
	
	OPAQUE,
	CUTOUT,
	TRANSPARENT;
	
	public static final int COUNT = values().length;
	
}
