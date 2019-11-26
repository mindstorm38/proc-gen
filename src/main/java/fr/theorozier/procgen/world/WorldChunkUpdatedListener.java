package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;

public interface WorldChunkUpdatedListener {

	void worldChunkUpdated(WorldChunk chunk, int x, int y, int z, Block block);
	void worldChunkUpdated(WorldChunk chunk);
	
}
