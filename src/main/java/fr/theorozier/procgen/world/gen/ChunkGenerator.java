package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.WorldChunk;

public abstract class ChunkGenerator {

	protected final long seed;
	
	public ChunkGenerator(long seed) {
		this.seed = seed;
	}
	
	public abstract void gen(WorldChunk chunk, int x, int y, int z);

}
