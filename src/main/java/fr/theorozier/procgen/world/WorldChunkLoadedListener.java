package fr.theorozier.procgen.world;

import fr.theorozier.procgen.world.chunk.Chunk;

public interface WorldChunkLoadedListener {
	
	void worldChunkLoaded(World world, Chunk chunk);
	void worldChunkUnloaded(World world, Chunk chunk);
	
}
