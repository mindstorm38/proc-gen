package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.WorldBlockPosition;
import fr.theorozier.procgen.world.WorldChunk;

public abstract class ChunkGenerator {

	protected final long seed;
	protected final BiomeProvider biomeProvider;
	
	public ChunkGenerator(long seed, BiomeProvider biomeProvider) {
		
		this.seed = seed;
		this.biomeProvider = biomeProvider;
		
	}
	
	public abstract void genBase(WorldChunk chunk, WorldBlockPosition pos);
	public abstract void genSurface(WorldChunk chunk, WorldBlockPosition pos);
	public abstract void genFeatures(WorldChunk chunk, WorldBlockPosition pos);
	
}
