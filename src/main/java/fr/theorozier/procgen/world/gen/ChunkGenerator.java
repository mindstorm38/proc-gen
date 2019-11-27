package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.WorldBlockPosition;
import fr.theorozier.procgen.world.WorldChunk;

public abstract class ChunkGenerator {

	protected final long seed;
	
	public ChunkGenerator(long seed) {
		this.seed = seed;
	}
	
	public abstract void genBase(WorldChunk chunk, WorldBlockPosition pos);
	public abstract void genSurface(WorldChunk chunk, WorldBlockPosition pos);
	public abstract void genFeatures(WorldChunk chunk, WorldBlockPosition pos);
	
}
