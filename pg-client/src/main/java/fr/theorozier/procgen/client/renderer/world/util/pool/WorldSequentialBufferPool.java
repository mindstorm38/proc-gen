package fr.theorozier.procgen.client.renderer.world.util.pool;

import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldSequentialBuffer;
import io.sutil.pool.GrowingObjectPool;

public class WorldSequentialBufferPool extends GrowingObjectPool<WorldSequentialBuffer> {
	
	public WorldSequentialBufferPool(int initialSize) {
		super(WorldSequentialBuffer::new, initialSize, 0, true);
	}
	
}
