package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;

@FunctionalInterface
public interface ChunkRedrawFunction {
	void redraw(WorldChunk chunk, ChunkRenderBuffers buffers);
}
