package fr.theorozier.procgen.world;

public interface WorldChunkLoadedListener {
	
	void worldChunkLoaded(World world, WorldChunk chunk);
	void worldChunkUnloaded(World world, WorldChunk chunk);
	
}
