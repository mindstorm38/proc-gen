package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;

public class ChunkRedrawUpload {

	private final ChunkRenderer renderer;
	private final ChunkRenderBuffers buffers;

	public ChunkRedrawUpload(ChunkRenderer renderer, ChunkRenderBuffers buffers) {
		this.renderer = renderer;
		this.buffers = buffers;
	}
	
	public void uploadAndRelease(ChunkRenderManager manager) {
		this.renderer.uploadRedrawBuffers(this.buffers);
		manager.releaseRenderBuffers(this.buffers);
	}
	
}
