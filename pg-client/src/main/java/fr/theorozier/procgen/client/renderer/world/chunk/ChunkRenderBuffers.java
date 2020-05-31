package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.client.renderer.buffer.WorldRenderSequentialBuffer;
import fr.theorozier.procgen.common.block.BlockRenderLayer;

public class ChunkRenderBuffers {

	private final WorldRenderSequentialBuffer[] buffers = new WorldRenderSequentialBuffer[BlockRenderLayer.COUNT];

	public ChunkRenderBuffers() {
		this.setBuffer(BlockRenderLayer.OPAQUE, new WorldRenderSequentialBuffer(), 24576);
		this.setBuffer(BlockRenderLayer.CUTOUT, new WorldRenderSequentialBuffer(), 18432);
		this.setBuffer(BlockRenderLayer.TRANSPARENT, new WorldRenderSequentialBuffer(), 6144);
	}
	
	private void setBuffer(BlockRenderLayer layer, WorldRenderSequentialBuffer buffer, int facesCapacity) {
		this.buffers[layer.ordinal()] = buffer;
		buffer.allocFaces(facesCapacity);
	}
	
	public WorldRenderSequentialBuffer getBuffer(int layer) {
		return this.buffers[layer];
	}
	
	public WorldRenderSequentialBuffer getBuffer(BlockRenderLayer layer) {
		return this.buffers[layer.ordinal()];
	}
	
	public void free() {
		for (int i = 0; i < this.buffers.length; ++i) {
			this.buffers[i].free();
			this.buffers[i] = null;
		}
	}
	
	public int getTotalBytes() {
		int total = 0;
		for (int i = 0; i < this.buffers.length; ++i) {
			total += this.buffers[i].getTotalBytes();
		}
		return total;
	}
	
}
