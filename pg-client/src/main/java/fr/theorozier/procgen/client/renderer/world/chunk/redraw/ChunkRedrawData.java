package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

public class ChunkRedrawData {
	
	private final ChunkRenderManager manager;
	private final ChunkRenderer cr;
	private final AbsBlockPosition pos;
	
	private ChunkRenderBuffers buffers = null;
	
	public ChunkRedrawData(ChunkRenderManager manager, ChunkRenderer cr) {
		
		if (!cr.isActive()) {
			throw new IllegalArgumentException("Given ChunkRenderer is not active, so can't be used for ChunkRedrawData.");
		}
		
		this.manager = manager;
		this.cr = cr;
		this.pos = cr.getChunkPosition();
		
	}
	
	private ChunkRenderBuffers getBuffers() {
		return this.buffers == null ? (this.buffers = this.manager.takeRenderBuffers()) : this.buffers;
	}
	
	private boolean isPositionCoherent() {
		return this.pos.equals(this.cr.getChunkPosition());
	}
	
	public PriorityRunnable newTask(ChunkRedrawFunction function) {
		return new Task(function);
	}
	
	public void upload() {
		if (this.isPositionCoherent() && this.buffers != null) {
			this.cr.uploadRedrawBuffers(this.buffers);
		}
	}
	
	public void free() {
		if (this.buffers != null) {
			this.manager.putRenderBuffers(this.buffers);
			this.buffers = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (this.buffers != null) {
			System.out.println("Loosed buffers " + this.buffers + " in " + this); // FIXME, WHY THERE ARE FUCKING LEAKS HERE ?
		}
	}
	
	private class Task implements PriorityRunnable {
		
		private final ChunkRedrawFunction function;
		
		public Task(ChunkRedrawFunction function) {
			this.function = function;
		}
		
		@Override
		public void run() {
			ChunkRedrawData data = ChunkRedrawData.this;
			if (data.isPositionCoherent()) {
				this.function.redraw(data.cr.getChunk(), data.getBuffers());
			}
		}
		
		@Override
		public int getPriority() {
			return (int) ChunkRedrawData.this.cr.getDistanceToCameraSquared();
		}
		
	}

}
