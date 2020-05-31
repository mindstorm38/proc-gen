package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;

public class ChunkRedrawTask implements Runnable {
	
	private final ChunkRenderer cr;
	private final ChunkRedrawFunction function;
	
	public ChunkRedrawTask(ChunkRenderer cr, ChunkRedrawFunction function) {
		this.cr = cr;
		this.function = function;
	}
	
	@Override
	public void run() {
	
	}
	
}
