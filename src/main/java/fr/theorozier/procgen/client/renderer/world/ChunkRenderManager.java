package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.layer.ChunkDirectLayerData;
import fr.theorozier.procgen.client.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.client.renderer.world.layer.ChunkLayerDataProvider;
import fr.theorozier.procgen.client.renderer.world.layer.ChunkSortedLayerData;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import io.msengine.client.util.camera.Camera3D;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChunkRenderManager {
	
	// These distances are squared, for optimisation.
	public static final int RENDER_DISTANCE_CHUNKS = 12;
	public static final int UNLOAD_DISTANCE_CHUNKS = 16;
	
	public static final int RENDER_DISTANCE = RENDER_DISTANCE_CHUNKS * 16;
	public static final int UNLOAD_DISTANCE = UNLOAD_DISTANCE_CHUNKS * 16;
	
	public static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;
	public static final int UNLOAD_DISTANCE_SQUARED = UNLOAD_DISTANCE * UNLOAD_DISTANCE;
	
	private final WorldRenderer renderer;
	
	private final BlockPosition cachedBlockPos;
	
	private final Map<BlockPositioned, ChunkRenderer> chunkRenderers;
	private final List<ChunkRenderer> chunkRenderersList;
	private final List<ImmutableBlockPosition> unloadingChunkRenderers;
	
	private final ChunkLayerDataProvider[] layerHandlers;
	
	private final ExecutorService chunkComputer;
	private final HashMap<ChunkUpdateDescriptor, Future<ChunkUpdateDescriptor>> chunkUpdates;
	private final List<Future<ChunkUpdateDescriptor>> chunkUpdatesDescriptors;
	
	private float viewX, viewY, viewZ;
	
	ChunkRenderManager(WorldRenderer renderer) {
		
		this.renderer = renderer;
		
		this.cachedBlockPos = new BlockPosition();
		
		this.chunkRenderers = new HashMap<>();
		this.chunkRenderersList = new ArrayList<>();
		this.unloadingChunkRenderers = new ArrayList<>();
		
		this.layerHandlers = new ChunkLayerDataProvider[BlockRenderLayer.COUNT];
		this.setLayerHandler(BlockRenderLayer.OPAQUE, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT_NOT_CULLED, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.TRANSPARENT, ChunkSortedLayerData::new);
		
		this.chunkComputer = Executors.newFixedThreadPool(2);
		this.chunkUpdates = new HashMap<>();
		this.chunkUpdatesDescriptors = new ArrayList<>();
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.renderer;
	}
	
	private void setLayerHandler(BlockRenderLayer layer, ChunkLayerDataProvider handler) {
		this.layerHandlers[layer.ordinal()] = handler;
	}
	
	public ChunkLayerData provideLayerData(BlockRenderLayer layer, WorldChunk chunk) {
		return this.layerHandlers[layer.ordinal()].provide(chunk, layer, this);
	}
	
	private void resortChunkRenderers() {
		this.chunkRenderersList.sort(ChunkRenderer::compareTo);
	}
	
	void render(BlockRenderLayer layer) {
		this.chunkRenderersList.forEach(cr -> cr.render(layer, RENDER_DISTANCE_SQUARED));
	}
	
	void update() {
		
		Iterator<Future<ChunkUpdateDescriptor>> chunkUpdatesIt = this.chunkUpdatesDescriptors.iterator();
		Future<ChunkUpdateDescriptor> future;
		ChunkUpdateDescriptor descriptor = null;
		ChunkRenderer renderer = null;
		
		while (chunkUpdatesIt.hasNext()) {
			
			future = chunkUpdatesIt.next();
			
			if (future.isDone()) {
				
				try {
					
					descriptor = future.get();
					renderer = this.chunkRenderers.get(descriptor.getChunkPosition());
					// TODO renderer is sometimes NULL
					
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				
				if (renderer != null) {
					renderer.chunkUpdateDone(descriptor.getRenderLayer());
				}
				
				chunkUpdatesIt.remove();
				
				if (descriptor != null) {
					this.chunkUpdates.remove(descriptor);
				}
				
			}
			
		}
		
		this.chunkRenderersList.forEach(ChunkRenderer::update);
		
	}
	
	void unload() {
		
		this.chunkRenderersList.forEach(ChunkRenderer::delete);
		this.chunkRenderersList.clear();
		this.chunkRenderers.clear();
		
	}
	
	private void loadChunkRenderer(WorldChunk chunk) {
		
		ImmutableBlockPosition pos = chunk.getChunkPos();
		ChunkRenderer cr = this.chunkRenderers.get(pos);
		
		if (cr == null) {
			
			//System.out.println("Loading chunk at " + pos);
			
			//System.out.println("Loading chunk at " + pos + " (chunk in the middle : " + chunk.getBlockAt(7, 7, 7) + ")");
			
			cr = new ChunkRenderer(this, chunk);
			this.chunkRenderers.put(pos, cr);
			this.chunkRenderersList.add(cr);
			
			this.resortChunkRenderers();
			
			cr.init();
			
			ChunkRenderer neighbour;
			for (Direction dir : Direction.values()) {
				if ((neighbour = this.chunkRenderers.get(this.cachedBlockPos.set(pos, dir.rx, dir.ry, dir.rz))) != null) {
					
					neighbour.setNeedUpdate(true);
					
					neighbour.setNeighbour(dir.oposite(), cr);
					cr.setNeighbour(dir, neighbour);
					
				}
			}
			
		}
		
	}
	
	private void deleteChunkRenderer(ImmutableBlockPosition pos) {
		
		ChunkRenderer cr = this.chunkRenderers.get(pos);
		
		if (cr != null) {
			
			//System.out.println("Unloading chunk at " + pos);
			
			this.chunkRenderers.remove(cr.getChunkPosition());
			this.chunkRenderersList.remove(cr);
			cr.delete();
			
		}
		
	}
	
	void updateViewPosition(Camera3D cam) {
		this.updateViewPosition(cam.getX(), cam.getY(), cam.getZ());
	}
	
	void updateViewPosition(float x, float y, float z) {
		
		this.viewX = x;
		this.viewY = y;
		this.viewZ = z;
		
		int ix = MathUtils.fastfloor(x);
		int iy = MathUtils.fastfloor(y);
		int iz = MathUtils.fastfloor(z);
		
		this.chunkRenderers.forEach((pos, cr) -> {
			
			if (cr.updateDistanceToCamera(x, y, z) > UNLOAD_DISTANCE_SQUARED) {
				this.unloadingChunkRenderers.add(pos.immutableBlockPos());
			} else {
				cr.updateViewPosition(ix, iy, iz);
			}
			
		});
		
		if (!this.unloadingChunkRenderers.isEmpty()) {
			
			this.unloadingChunkRenderers.forEach(this::deleteChunkRenderer);
			this.unloadingChunkRenderers.clear();
			
		}
		
		this.resortChunkRenderers();
		
		WorldClient world = this.renderer.getRenderingWorld();
		
		if (world != null) {
			
			world.forEachChunkNear(x, y, z, RENDER_DISTANCE_CHUNKS, chunk -> {
			
				if (chunk.getDistSquaredTo(x, y, z) <= RENDER_DISTANCE_SQUARED)
					this.loadChunkRenderer(chunk);
			
			});
			
		}
		
	}
	
	// Update tasks
	
	public void scheduleUpdateTask(ChunkRenderer cr, BlockRenderLayer layer, Runnable action) {
		
		ChunkUpdateDescriptor descriptor = new ChunkUpdateDescriptor(cr.getChunkPosition(), layer);
		
		if (!this.chunkUpdates.containsKey(descriptor)) {
			
			Future<ChunkUpdateDescriptor> future = this.chunkComputer.submit(action, descriptor);
			this.chunkUpdates.put(descriptor, future);
			this.chunkUpdatesDescriptors.add(future);
			
		}
		
	}
	
	// Events //
	
	void chunkLoaded(WorldChunk chunk) {
		
		if (chunk.getDistSquaredTo(this.viewX, this.viewY, this.viewZ) <= RENDER_DISTANCE_SQUARED)
			this.loadChunkRenderer(chunk);
		
	}
	
	void chunkUnloaded(ImmutableBlockPosition pos) {
		this.deleteChunkRenderer(pos);
	}
	
	void chunkUpdated(WorldChunk chunk) {
		
		ChunkRenderer renderer = this.chunkRenderers.get(chunk.getChunkPos());
		if (renderer != null) renderer.chunkUpdated(chunk);
		
	}
	
	void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState state) {
		
		ChunkRenderer renderer = this.chunkRenderers.get(chunk.getChunkPos());
		if (renderer != null) renderer.blockUpdated(chunk, pos, state);
		
	}
	
}
