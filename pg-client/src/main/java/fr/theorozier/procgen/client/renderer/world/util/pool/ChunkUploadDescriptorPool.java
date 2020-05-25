package fr.theorozier.procgen.client.renderer.world.util.pool;

import fr.theorozier.procgen.client.renderer.world.chunk.ChunkUploadDescriptor;
import io.sutil.pool.GrowingObjectPool;

public class ChunkUploadDescriptorPool extends GrowingObjectPool<ChunkUploadDescriptor> {
	
	public ChunkUploadDescriptorPool(int initialSize) {
		super(ChunkUploadDescriptor::new, initialSize, 0, true);
	}
	
}
