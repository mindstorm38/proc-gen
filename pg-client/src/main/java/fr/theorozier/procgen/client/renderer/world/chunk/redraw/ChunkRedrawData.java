package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class ChunkRedrawData {
	
	private static final AtomicInteger UID = new AtomicInteger();
	
	private final int tmpUid = UID.getAndIncrement();
	
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
	
	private void debug(String msg) {
		System.out.println("CRD#" + this.tmpUid + ": " + msg);
	}
	
	private ChunkRenderBuffers getBuffers() {
		boolean taking = this.buffers == null;
		if (taking) debug("takeRenderBuffers");
		ChunkRenderBuffers ret = null;
		try {
			ret = this.buffers == null ? (this.buffers = this.manager.takeRenderBuffers()) : this.buffers;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (taking) debug("takeRenderBuffers:done");
		return ret;
	}
	
	private boolean isPositionCoherent() {
		return this.pos.equals(this.cr.getChunkPosition());
	}
	
	public PriorityRunnable newTask(ChunkRedrawFunction function) {
		return this.new Task(function);
	}
	
	public void upload() {
		debug("upload");
		if (this.isPositionCoherent() && this.buffers != null) {
			debug("upload/coherent");
			this.cr.uploadRedrawBuffers(this.buffers);
		}
	}
	
	public void free() {
		
		debug("free");
		this.cr.doneRedrawing();
		
		if (this.buffers != null) {
			debug("free/buffer");
			this.manager.putRenderBuffers(this.buffers);
			this.buffers = null;
		}
		
	}
	
	@Override
	protected void finalize() {
		if (this.buffers != null) {
			debug("lost buffer: " + this.buffers); // FIXME, WHY THERE ARE FUCKING LEAKS HERE ?
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
