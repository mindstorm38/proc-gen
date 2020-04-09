package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkUpdateDescriptor;
import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.chunk.layer.ChunkDirectLayerData;
import fr.theorozier.procgen.client.renderer.world.chunk.layer.ChunkLayerData;
import fr.theorozier.procgen.client.renderer.world.chunk.layer.ChunkLayerDataProvider;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.util.camera.SmoothCamera3D;
import io.msengine.common.util.GameProfiler;
import io.sutil.math.MathHelper;
import io.sutil.profiler.Profiler;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Chunk render manager singleton, instantiated in {@link WorldRenderer}.
 *
 * @author Theo Rozier
 *
 */
public class ChunkRenderManager {
	
	private static final ThreadingDispatch CHUNK_RENDERER_DISPATCH = ThreadingDispatch.register("CHUNK_RENDER", 3);
	private static final Profiler PROFILER = GameProfiler.getInstance();
	
	// Local cache for global singletons
	private final WorldRenderer renderer;
	private final ModelHandler model;
	
	// Chunk renderers for each chunks
	private final List<ChunkRenderer> chunkRenderers = new ArrayList<>();
	private final Deque<ChunkRenderer> availableChunkRenderers = new ArrayDeque<>();
	private final Map<AbsBlockPosition, ChunkRenderer> usedChunkRenderers = new HashMap<>();
	private final List<ImmutableBlockPosition> releasingChunkRenderers = new ArrayList<>();
	
	// Chunk layer providers
	private final ChunkLayerDataProvider[] layerHandlers = new ChunkLayerDataProvider[BlockRenderLayer.COUNT];
	
	// Tasks managing
	private ExecutorService threadPool = null;
	private final HashMap<ChunkUpdateDescriptor, Future<ChunkUpdateDescriptor>> chunkUpdates = new HashMap<>();
	private final List<Future<ChunkUpdateDescriptor>> chunkUpdatesDescriptors = new ArrayList<>();
	
	// Cached block position used to avoid repetitive reallocations
	private final BlockPosition cachedBlockPos = new BlockPosition();
	
	// Render distance in chunks, and the squared distance in blocks
	private int renderDistance = 0;
	private int renderDistanceSquared = 0;
	private int unloadDistanceSquared = 0;
	
	private float viewX, viewY, viewZ;
	
	public ChunkRenderManager(WorldRenderer renderer) {
		
		this.renderer = renderer;
		this.model = renderer.getModelHandler();
		
		this.setLayerHandler(BlockRenderLayer.OPAQUE, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT_NOT_CULLED, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.TRANSPARENT, ChunkDirectLayerData::new);
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.renderer;
	}
	
	private void setLayerHandler(BlockRenderLayer layer, ChunkLayerDataProvider handler) {
		this.layerHandlers[layer.ordinal()] = handler;
	}
	
	public ChunkLayerData provideLayerData(BlockRenderLayer layer) {
		return this.layerHandlers[layer.ordinal()].provide(layer, this);
	}
	
	/**
	 * <p>Initialize the chunk render manager, this create the new thread pool for chunk render data recomputation.</p>
	 * <p><b>Must only be called from {@link WorldRenderer}.</b></p>
	 */
	void init() {
		
		int poolSize = CHUNK_RENDERER_DISPATCH.getEffectiveCount();
		
		LOGGER.info("Starting world chunk renderer tasks thread pool (" + poolSize + " threads) ...");
		this.threadPool = Executors.newFixedThreadPool(CHUNK_RENDERER_DISPATCH.getEffectiveCount());
		
	}
	
	/**
	 * <p>Stop and shutdown internal thread pool.</p>
	 * <p><b>Must only be called from {@link WorldRenderer}.</b></p>
	 */
	void stop() {
		
		LOGGER.info("Shutting down chunk renderer tasks thread pool ...");
		this.threadPool.shutdown(); // FIXME Potential leaks if never finished (case not confirmed)...
		this.threadPool = null;
		
	}
	
	/**
	 * <p>Set internal render distance and then create missing {@link ChunkRenderer} or delete those too many.</p>
	 * <p><b>Must only be called from {@link WorldRenderer} while initialized.</b></p>
	 * @param renderDistance The render distance, in chunks.
	 */
	void setRenderDistance(int renderDistance) {
		
		this.disableRenderDistance();
		
		this.renderDistance = renderDistance;
		this.renderDistanceSquared = (renderDistance << 4) * (renderDistance << 4);
		this.unloadDistanceSquared = ((renderDistance + 2) << 4) * ((renderDistance + 2) << 4);
		
		int length = (this.renderDistance << 1) + 1;
		
		// Multiply by a large approximation of the ratio of volume taken by a sphere in a cube.
		int renderersCount = MathHelper.floorFloatInt(0.7f * length * length * length);
		System.out.println("Chunk renderer manager render distance : " + renderDistance + " ; renderers count : " + renderersCount);
		ChunkRenderer renderer;
		for (int i = 0; i < renderersCount; ++i) {
			
			renderer = new ChunkRenderer(this);
			this.chunkRenderers.add(renderer);
			this.availableChunkRenderers.add(renderer);
			renderer.init();
			
		}
		
		this.allocateChunkRenderersNear(this.viewX, this.viewY, this.viewZ);
		
	}
	
	/**
	 * <p>Disable render distance, so all chunk renderers are deleted.</p>
	 * <p><b>Must only be called from {@link WorldRenderer} while initialized.</b></p>
	 */
	void disableRenderDistance() {
		
		this.renderDistance = 0;
		this.chunkRenderers.forEach(ChunkRenderer::delete);
		this.chunkRenderers.clear();
		this.availableChunkRenderers.clear();
		this.usedChunkRenderers.clear();
		
	}
	
	/**
	 * @return True if the manager is ready and had a valid render distance.
	 */
	public boolean isReady() {
		return this.renderDistance != 0;
	}
	
	/**
	 * Render a specific layer on the current framebuffer.
	 * Layer are used by {@link WorldRenderer} to change render properties for each layers.
	 * @param layer The block render layer to render.
	 */
	void render(BlockRenderLayer layer, float camX, float camZ) {
		
		// if (this.isReady()) { FIXME Commented to avoid double-check of isReady() here and in WorldRenderer that have the same renderDistance.
		
		PROFILER.startSection("render_layer_" + layer.name());
		this.chunkRenderers.forEach(cr -> cr.render(layer, this.renderDistanceSquared, camX, camZ));
		this.model.apply(); // Apply the last model.pop() in cr.render(...)
		PROFILER.endSection();
		
		// }
		
	}
	
	/**
	 * To run every tick.
	 */
	void update() {
		
		PROFILER.startSection("chunk_render_update");
		
		Iterator<Future<ChunkUpdateDescriptor>> chunkUpdatesIt = this.chunkUpdatesDescriptors.iterator();
		Future<ChunkUpdateDescriptor> future;
		ChunkUpdateDescriptor descriptor;
		ChunkRenderer renderer;
		
		while (chunkUpdatesIt.hasNext()) {
			
			future = chunkUpdatesIt.next();
			
			if (future.isDone()) {
				
				try {
					
					descriptor = future.get();
					renderer = this.usedChunkRenderers.get(descriptor.getChunkPosition());
					// TODO renderer is sometimes NULL
					
					if (renderer != null) {
						renderer.chunkUpdateDone(descriptor.getRenderLayer());
					}
					
					this.chunkUpdates.remove(descriptor);
					
				} catch (InterruptedException | ExecutionException e) {
					if (e.getCause() != null) {
						e.getCause().printStackTrace();
					}
				}
				
				chunkUpdatesIt.remove();
				
			}
			
		}
		
		this.chunkRenderers.forEach(ChunkRenderer::update);
		
		PROFILER.endSection();
		
	}
	
	/**
	 * Unload chunk renderers for current world.
	 */
	void unload() {
		
		this.chunkRenderers.forEach(ChunkRenderer::delete);
		this.chunkRenderers.clear();
		this.usedChunkRenderers.clear();
		
	}
	
	/**
	 * Allocate a free {@link ChunkRenderer} for a specified chunk.
	 * @param chunk The chunk.
	 * @return The new allocated chunk renderer, or <b>NULL</b> if there isn't free renderer.
	 */
	private ChunkRenderer allocateChunkRenderer(WorldChunk chunk) {
		
		ImmutableBlockPosition pos = chunk.getChunkPos();
		ChunkRenderer cr = this.usedChunkRenderers.get(pos);
		
		if (cr != null)
			return cr;
		
		cr = this.availableChunkRenderers.poll();
		
		if (cr != null) {
			
			ChunkRenderer neighbour;
			for (Direction dir : Direction.values()) {
				if ((neighbour = this.usedChunkRenderers.get(this.cachedBlockPos.set(pos, dir))) != null) {
					
					neighbour.setNeedUpdate(true);
					neighbour.setNeighbour(dir.oposite(), cr);
					cr.setNeighbour(dir, neighbour);
					
				}
			}
			
			this.usedChunkRenderers.put(pos, cr);
			
			cr.setChunk(chunk);
			cr.updateDistanceToCamera(this.viewX, this.viewY, this.viewZ);
			
		}
		
		return cr;
		
	}
	
	/**
	 * Mark a previously active {@link ChunkRenderer} as free for new allocations.
	 * @param pos The position of the chunk to free.
	 */
	private void releaseChunkRenderer(AbsBlockPosition pos) {
		
		ChunkRenderer renderer = this.usedChunkRenderers.remove(pos);
		
		if (renderer != null) {
			
			renderer.releaseChunk();
			renderer.removeAllNeighbours((dir, neighbour) -> neighbour.removeNeighbour(dir.oposite()));
			this.availableChunkRenderers.add(renderer);
			// FIXME Do neighbours need setNeedUpdate(true) ?
			
		}
		
	}
	
	void updateViewPosition(SmoothCamera3D cam) {
		this.updateViewPosition(cam.getTargetX(), cam.getTargetY(), cam.getTargetZ());
	}
	
	void updateViewPosition(float x, float y, float z) {
		
		this.viewX = x;
		this.viewY = y;
		this.viewZ = z;
		
		int ix = MathHelper.floorFloatInt(x);
		int iy = MathHelper.floorFloatInt(y);
		int iz = MathHelper.floorFloatInt(z);
		
		this.usedChunkRenderers.forEach((pos, cr) -> {
			
			if (cr.updateDistanceToCamera(x, y, z) > this.unloadDistanceSquared) {
				this.releasingChunkRenderers.add(pos.immutableBlockPos());
			} else {
				cr.updateViewPosition(ix, iy, iz);
			}
			
		});
		
		if (!this.releasingChunkRenderers.isEmpty()) {
			
			this.releasingChunkRenderers.forEach(this::releaseChunkRenderer);
			this.releasingChunkRenderers.clear();
			
		}
		
		this.allocateChunkRenderersNear(x, y, z);
		
	}
	
	private void allocateChunkRenderersNear(float x, float y, float z) {
		
		WorldClient world = this.renderer.getRenderingWorld();
		
		if (world != null) {
			
			world.forEachChunkNear(x, y, z, this.renderDistance, chunk -> {
				
				if (chunk.getDistSquaredTo(x, y, z) <= this.renderDistanceSquared)
					this.allocateChunkRenderer(chunk);
				
			});
			
		}
		
	}
	
	// Update tasks
	
	public void scheduleUpdateTask(ChunkRenderer cr, BlockRenderLayer layer, Runnable action) {
		
		if (this.threadPool == null)
			throw new IllegalStateException("Can't schedule update tasks while associated thread pool is not initialized.");
		
		ChunkUpdateDescriptor descriptor = new ChunkUpdateDescriptor(cr.getChunkPosition(), layer);
		
		if (!this.chunkUpdates.containsKey(descriptor)) {
			
			Future<ChunkUpdateDescriptor> future = this.threadPool.submit(action, descriptor);
			this.chunkUpdates.put(descriptor, future);
			this.chunkUpdatesDescriptors.add(future);
			
		}
		
	}
	
	// Events //
	
	void chunkLoaded(WorldChunk chunk) {
		
		if (chunk.getDistSquaredTo(this.viewX, this.viewY, this.viewZ) <= this.renderDistanceSquared)
			this.allocateChunkRenderer(chunk);
		
	}
	
	void chunkUnloaded(AbsBlockPosition pos) {
		this.releaseChunkRenderer(pos);
	}
	
	void chunkUpdated(WorldChunk chunk) {
		
		ChunkRenderer renderer = this.usedChunkRenderers.get(chunk.getChunkPos());
		if (renderer != null) renderer.chunkUpdated(chunk);
		
	}
	
	void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState state) {
		
		ChunkRenderer renderer = this.usedChunkRenderers.get(chunk.getChunkPos());
		if (renderer != null) renderer.blockUpdated(chunk, pos, state);
		
	}
	
}
