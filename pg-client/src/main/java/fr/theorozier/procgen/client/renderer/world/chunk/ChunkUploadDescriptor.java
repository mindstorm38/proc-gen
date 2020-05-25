package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldSequentialBuffer;
import fr.theorozier.procgen.client.renderer.world.util.pool.WorldSequentialBufferPool;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import io.sutil.pool.ObjectPool;

@SuppressWarnings("rawtypes")
public class ChunkUploadDescriptor {
	
	private final ObjectPool.PoolObject[] buffers = new ObjectPool.PoolObject[BlockRenderLayer.COUNT];
	
	public WorldSequentialBuffer getBuffer(BlockRenderLayer layer, WorldSequentialBufferPool pool) {
		
		ObjectPool.PoolObject obj = this.buffers[layer.ordinal()];
		
		if (obj == null) {
			obj = pool.acquire();
			this.buffers[layer.ordinal()] = obj;
		}
		
		return (WorldSequentialBuffer) obj.get();
		
	}
	
	public void clearBuffer(BlockRenderLayer layer, WorldSequentialBufferPool pool) {
		this.getBuffer(layer, pool).clear();
	}
	
	public void clearBufferIfPresent(BlockRenderLayer layer) {
		
		ObjectPool.PoolObject obj = this.buffers[layer.ordinal()];
		
		if (obj != null) {
			((WorldSequentialBuffer) obj.get()).clear();
		}
		
	}
	
	public WorldSequentialBuffer getBuffer(BlockRenderLayer layer) {
		return this.getBuffer(layer.ordinal());
	}
	
	public WorldSequentialBuffer getBuffer(int layerIdx) {
		ObjectPool.PoolObject obj = this.buffers[layerIdx];
		return obj == null ? null : (WorldSequentialBuffer) obj.get();
	}
	
	@SuppressWarnings("unchecked")
	public void releaseAllBuffers(WorldSequentialBufferPool pool) {
		ObjectPool.PoolObject obj;
		for (int i = 0; i < this.buffers.length; ++i) {
			if ((obj = this.buffers[i]) != null) {
				pool.release(obj);
			}
		}
	}
	
	/*
	private final LayerDescriptor[] layers = new LayerDescriptor[BlockRenderLayer.COUNT];
	
	public void clear() {
		for (LayerDescriptor descriptor : this.layers) {
			if (descriptor != null) {
				descriptor.bufferObject = null;
			}
		}
	}
	
	private LayerDescriptor getLayerDescriptor(BlockRenderLayer layer, boolean create) {
		LayerDescriptor descriptor = this.layers[layer.ordinal()];
		if (descriptor != null) {
			return descriptor;
		} else {
			return create ? (this.layers[layer.ordinal()] = new LayerDescriptor()) : null;
		}
	}
	
	public WorldSequentialBufferPool.PoolObject getLayerBufferObject(BlockRenderLayer layer) {
		LayerDescriptor descriptor = this.getLayerDescriptor(Objects.requireNonNull(layer), false);
		return descriptor == null ? null : descriptor.bufferObject;
	}
	
	public WorldSequentialBufferPool.PoolObject getLayerBufferObjectCreate(BlockRenderLayer layer, WorldSequentialBufferPool pool) {
		Objects.requireNonNull(pool);
		return Objects.requireNonNull(this.getLayerDescriptor(Objects.requireNonNull(layer), true)).ensureBufferObject(pool);
	}
	
	public void addUploadSegment(BlockRenderLayer layer, int from, int fromLen, int to, int toReplaceLen) {
		Objects.requireNonNull(this.getLayerDescriptor(layer, true)).addUploadSegment(from, fromLen, to, toReplaceLen);
	}
	
	public void addUploadSegmentAll(BlockRenderLayer layer) {
		this.addUploadSegment(layer, 0, 0, 0, 0);
	}
	
	public void forEachUploadSegment(SegmentConsumer consumer) {
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			LayerDescriptor descriptor = this.layers[layer.ordinal()];
			if (descriptor != null) {
				descriptor.uploadSegments.forEach(seg -> {
					int from = LayerDescriptor.getSegmentFrom(seg);
					int fromLen = LayerDescriptor.getSegmentFromLen(seg);
					int to = LayerDescriptor.getSegmentTo(seg);
					int toReplaceLen = LayerDescriptor.getSegmentToReplaceLen(seg);
					consumer.accept(layer, descriptor.getBufferOrNull(), from, fromLen, to, toReplaceLen);
				});
			}
		}
	}
	
	private static class LayerDescriptor {
		
		private WorldSequentialBufferPool.PoolObject bufferObject;
		private final List<Long> uploadSegments = new ArrayList<>();
		
		WorldSequentialBufferPool.PoolObject ensureBufferObject(WorldSequentialBufferPool pool) {
			
			if (this.bufferObject == null) {
				this.bufferObject = pool.acquire();
				this.bufferObject.get().clear();
			}
			
			return this.bufferObject;
			
		}
		
		public WorldSequentialBuffer getBufferOrNull() {
			return this.bufferObject == null ? null : this.bufferObject.get();
		}
		
		void clearSegments() {
			this.uploadSegments.clear();
		}
		
		void addUploadSegment(int from, int fromLen, int to, int toReplaceLen) {
			
			List<Long> segments = this.uploadSegments;
			long seg;
			
			for (int i = 0; i < segments.size(); ++i) {
				
				seg = segments.get(i);
				
				if (from == getSegmentFromEnd(seg)) {
					
					int newLen = getSegmentFromLen(seg) + fromLen;
					seg = changeSegmentFromLen(seg, newLen);
					
				}
				
			}
			
			this.uploadSegments.add(buildSegment(from, to, fromLen, toReplaceLen));
			
		}
		*/
		/**
		 * Segment format :
		 * <pre><code>
		 *   +----------------+---------------+--------------+-------------+
		 *   | TO_REPLACE_LEN |   FROM_LEN    |      TO      |     FROM    |
		 *   +----------------+---------------+--------------+-------------+
		 *   |    10 bits     |    10 bits    |    22 bits   |   22 bits   |
		 *   +----------------+---------------+--------------+-------------+
		 * </code></pre>
		 * @param from Offset in native buffer.
		 * @param fromLen Length to copy from native buffer.
		 * @param to Offset in GL buffer.
		 * @param toReplaceLen Length to remove in GL buffer.
		 * @return The 64 bits number compiling all parameters.
		 */
		/*static long buildSegment(int from, int fromLen, int to, int toReplaceLen) {
			return ((long) from & 0x3FFFFF) |
					((long) to & 0x3FFFFF) << 22 |
					((long) fromLen & 0x3FF) << 44 |
					((long) toReplaceLen & 0x3FF) << 54;
		}
		
		static long changeSegmentFromLen(long segment, int fromLen) {
			return (segment & ~(0x3FFL << 44)) | ((long) fromLen & 0x3FF) << 44;
		}
		
		static int getSegmentFrom(long segment) {
			return (int) segment & 0x3FFFFF;
		}
		
		static int getSegmentFromLen(long segment) {
			return (int) (segment >> 44) & 1023;
		}
		
		static int getSegmentTo(long segment) {
			return (int) (segment >> 22) & 0x3FFFFF;
		}
		
		static int getSegmentToReplaceLen(long segment) {
			return (int) (segment >> 54) & 1023;
		}
		
		static int getSegmentFromEnd(long segment) {
			return getSegmentFrom(segment) + getSegmentFromLen(segment);
		}
		
	}
	
	@FunctionalInterface
	public interface SegmentConsumer {
		void accept(BlockRenderLayer layer, WorldSequentialBuffer buff, int from, int fromLen, int to, int toReplaceLen);
	}*/
	
}
