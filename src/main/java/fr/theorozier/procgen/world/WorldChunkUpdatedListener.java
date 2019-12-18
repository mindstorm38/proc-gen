package fr.theorozier.procgen.world;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.world.chunk.Chunk;

public interface WorldChunkUpdatedListener {

	void worldChunkUpdated(Chunk chunk, int x, int y, int z, Block block);
	void worldChunkUpdated(Chunk chunk);
	
}
