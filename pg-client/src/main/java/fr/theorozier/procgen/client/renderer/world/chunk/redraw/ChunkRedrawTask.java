package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.common.util.concurrent.PriorityCallable;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;

import java.util.Objects;

public class ChunkRedrawTask implements PriorityCallable<ChunkRenderBuffers> {
	
	private final ChunkRenderManager manager;
	private final ChunkRenderer renderer;
	private final WorldChunk chunk;
	
	private final ChunkRedrawFunction func;
	
	public ChunkRedrawTask(ChunkRenderManager manager, ChunkRenderer renderer, ChunkRedrawFunction func) {
		this.manager = Objects.requireNonNull(manager);
		this.renderer = Objects.requireNonNull(renderer);
		this.chunk = Objects.requireNonNull(renderer.getChunk());
		this.func = Objects.requireNonNull(func);
	}
	
	private boolean isPositionCoherent() {
		return this.chunk.getChunkPos().equals(this.renderer.getChunkPosition());
	}
	
	@Override
	public ChunkRenderBuffers call() {
			
		if (this.isPositionCoherent()) {
			
			try {
				ChunkRenderBuffers buffers = this.manager.takeRenderBuffers();
				this.func.redraw(this.chunk, buffers);
				return buffers;
			} catch (InterruptedException e) {
				return null;
			}
			
		} else {
			return null;
		}
			
	}
	
	@Override
	public int getPriority() {
		return (int) this.renderer.getDistanceToCameraSquared();
	}
	
}
