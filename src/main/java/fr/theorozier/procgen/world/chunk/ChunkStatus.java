package fr.theorozier.procgen.world.chunk;

public enum ChunkStatus {
	
	EMPTY,
	BASE,
	SURFACE,
	DECORATIONS,
	FULL;
	
	public boolean atLeast(ChunkStatus status) {
		return this.ordinal() >= status.ordinal();
	}
	
}
