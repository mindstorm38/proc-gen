package fr.theorozier.procgen.common.world.event;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

public interface WorldChunkListener {
	
	void worldChunkUpdated(WorldBase world, WorldChunk chunk);
	void worldChunkBlockChanged(WorldBase world, WorldChunk chunk, BlockPositioned pos, BlockState state);
	
}
