package fr.theorozier.procgen.common.world.event;

import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

public interface WorldLoadingListener {
	
	void worldChunkLoaded(WorldBase world, WorldChunk chunk);
	void worldChunkUnloaded(WorldBase world, ImmutableBlockPosition position);
	
}
