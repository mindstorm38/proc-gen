package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.renderer.world.chunk.redraw.ChunkRedrawFunction;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderBuffers;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.client.renderer.world.chunk.redraw.ChunkRedrawTask;
import fr.theorozier.procgen.client.renderer.world.chunk.redraw.ChunkRedrawUpload;
import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

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
	
	private static final int TICK_MAX_CHUNK_UPDATES = 1024;
	private static final int TICK_MAX_CHUNK_UPLOAD = 1024;
	
	// Local cache for global singletons
	private final WorldRenderer renderer;
	private final ModelHandler model;
	
	// Chunk renderers for each chunks
	private final List<ChunkRenderer> chunkRenderers = new ArrayList<>();
	private final Deque<ChunkRenderer> availableChunkRenderers = new ArrayDeque<>();
	private final Map<AbsBlockPosition, ChunkRenderer> usedChunkRenderers = new HashMap<>();
	private final List<ImmutableBlockPosition> releasingChunkRenderers = new ArrayList<>();
	
	// Tasks managing
	private PriorityThreadPoolExecutor threadPool = null;
	private BlockingQueue<ChunkRenderBuffers> chunkRenderBuffers = null;
	private final List<Future<ChunkRedrawUpload>> chunkRedrawFutures = new ArrayList<>();
	
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
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.renderer;
	}
	
	/**
	 * <p>Initialize the chunk render manager, this create the new thread pool for chunk render data recomputation.</p>
	 * <p><b>Must only be called from {@link WorldRenderer}.</b></p>
	 */
	void init() {
		
		int poolSize = CHUNK_RENDERER_DISPATCH.getEffectiveCount() * 2;
		int renderBuffersSize = poolSize * 3;
		
		LOGGER.info("Starting world chunk renderer tasks thread pool (" + poolSize + " threads) ...");
		// this.threadPool = Executors.newFixedThreadPool(CHUNK_RENDERER_DISPATCH.getEffectiveCount());
		this.threadPool = new PriorityThreadPoolExecutor(poolSize, PriorityThreadPoolExecutor.ASC_COMPARATOR);
		this.chunkRenderBuffers = new ArrayBlockingQueue<>(renderBuffersSize);
		
		for (int i = 0; i < renderBuffersSize; ++i) {
			this.chunkRenderBuffers.add(new ChunkRenderBuffers());
		}
		
	}
	
	/**
	 * <p>Stop and shutdown internal thread pool.</p>
	 * <p><b>Must only be called from {@link WorldRenderer}.</b></p>
	 */
	void stop() {
		
		LOGGER.info("Shutting down chunk renderer tasks thread pool ...");
		this.threadPool.shutdown(); // FIXME Potential leaks if never finished (case not confirmed)...
		this.threadPool = null;
		
		this.chunkRenderBuffers.forEach(ChunkRenderBuffers::free);
		this.chunkRenderBuffers = null;
		
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
		
		int length = ((renderDistance + 2) << 1) + 1;
		
		// Multiply by a large approximation of the ratio of volume taken by a sphere in a cube.
		int renderersCount = MathHelper.floorFloatInt(0.7f * length * length * length);
		
		LOGGER.info("Chunk renderer manager render distance : " + renderDistance + " ; renderers count : " + renderersCount);
		
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
		
		PROFILER.startSection("render_layer_" + layer.name());
		this.chunkRenderers.forEach(cr -> cr.render(layer, this.renderDistanceSquared, camX, camZ));
		this.model.apply(); // Apply the last model.pop() in cr.render(...)
		PROFILER.endSection();
		
	}
	
	/**
	 * To run every tick.
	 */
	void update() {
		
		PROFILER.startSection("chunk_render_update");
		
		List<Future<ChunkRedrawUpload>> redrawFutures = this.chunkRedrawFutures;
		Future<ChunkRedrawUpload> redrawFuture;
		ChunkRedrawUpload redraw;
		//ChunkRedrawData redrawData;
		
		int actcounter = 0;
		
		for (int i = 0, size = redrawFutures.size(); i < size; ++i) {
			if ((redrawFuture = redrawFutures.get(i)).isDone()) {
				
				// redrawData = redrawFuture.getData();
				
				try {
					
					redraw = redrawFuture.get();
					redraw.uploadAndRelease(this);
					actcounter++;
					
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.log(Level.WARNING, "Redraw task interrupted.", e);
				} catch (CancellationException ignored) {
					//
				} finally {
					redrawFutures.remove(i--);
					size--;
				}
				
			}
		}
		
		ChunkRenderer cr;
		int size = this.chunkRenderers.size();
		actcounter = 0;
		
		for (int i = 0; i < size; ++i) {
			if (this.chunkRenderers.get(i).update()) {
				actcounter++;
			}
		}
		
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
		
		ImmutableBlockPosition pos = Objects.requireNonNull(chunk).getChunkPos();
		ChunkRenderer cr = this.usedChunkRenderers.get(pos);
		
		if (cr != null)
			return cr;
		
		cr = this.availableChunkRenderers.poll();
		
		if (cr != null) {
			
			ChunkRenderer neighbour;
			for (Direction dir : Direction.values()) {
				if ((neighbour = this.getChunkRendererNeighbour(chunk, dir)) != null) {
					neighbour.setNeedUpdate();
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
			
			if (renderer.isActive()) {
				
				WorldChunk chunk = renderer.getChunk();
				ChunkRenderer neighbour;
				for (Direction dir : Direction.values()) {
					if ((neighbour = this.getChunkRendererNeighbour(chunk, dir)) != null) {
						neighbour.setNeedUpdate();
					}
				}
				
				renderer.releaseChunk();
				
			}
			
			this.availableChunkRenderers.add(renderer);
			
		}
		
	}
	
	public int getUsedChunkRenderersCount() {
		return this.usedChunkRenderers.size();
	}
	
	public int getAvailableChunkRenderersCount() {
		return this.availableChunkRenderers.size();
	}
	
	public int getChunkRenderBuffersCount() {
		return this.chunkRenderBuffers.size();
	}
	
	public int getRunningRedrawTasksCount() {
		return this.chunkRedrawFutures.size();
	}
	
	public ChunkRenderer getChunkRendererNeighbour(WorldChunk chunk, Direction dir) {
		return this.usedChunkRenderers.get(this.cachedBlockPos.set(chunk.getChunkPos(), dir));
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
				// cr.updateViewPosition(ix, iy, iz);
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
				
				if (!this.usedChunkRenderers.containsKey(chunk.getChunkPos())) {
					if (chunk.getDistSquaredTo(x, y, z) <= this.renderDistanceSquared) {
						this.allocateChunkRenderer(chunk);
					}
				}
				
			});
			
		}
		
	}
	
	// Update tasks
	
	public Future<?> scheduleChunkRedrawTask(ChunkRenderer cr, ChunkRedrawFunction redrawFunc) {
		
		if (this.threadPool == null) {
			throw new IllegalStateException("Can't schedule update tasks while associated thread pool is not initialized.");
		}
		
		Future<ChunkRedrawUpload> future = this.threadPool.submit(new ChunkRedrawTask(this, cr, redrawFunc));
		this.chunkRedrawFutures.add(future);
		return future;
		
		/*ChunkRedrawData data = new ChunkRedrawData(this, cr);
		ChunkRedrawFuture<?> future = new ChunkRedrawFuture<>(data, this.threadPool.submit(data.newTask(redrawFunc)));
		this.chunkRedrawFutures.add(future);
		return future;*/
		
	}
	
	public ChunkRenderBuffers takeRenderBuffers() throws InterruptedException {
		return this.chunkRenderBuffers.take();
	}
	
	public void releaseRenderBuffers(ChunkRenderBuffers buffers) {
		if (!this.chunkRenderBuffers.offer(buffers)) {
			throw new IllegalStateException("Failed to put back the render buffers ! Be careful and only put back buffers acquired by takeRenderBuffers().");
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
	
	void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState state, BlockState previousState) {
		
		ChunkRenderer renderer = this.usedChunkRenderers.get(chunk.getChunkPos());
		if (renderer != null) renderer.blockUpdated(chunk, pos, state, previousState);
		
	}
	
}
