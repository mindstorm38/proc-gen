package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.layer.ChunkDirectLayerData;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerDataProvider;
import fr.theorozier.procgen.renderer.world.layer.ChunkSortedLayerData;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.util.camera.Camera3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkRenderManager {
	
	// These distances are squared, for optimisation.
	public static final int RENDER_DISTANCE = 16 * 16;
	public static final int UNLOAD_DISTANCE = 16 * 32;
	
	public static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;
	public static final int UNLOAD_DISTANCE_SQUARED = UNLOAD_DISTANCE * UNLOAD_DISTANCE;
	
	private final WorldRenderer renderer;
	
	private final Map<BlockPosition, ChunkRenderer> chunkRenderers;
	private final List<BlockPosition> unloadingChunkRenderers;
	
	private final ChunkLayerDataProvider[] layerHandlers;
	
	private final ExecutorService chunkComputer;
	
	private float viewX, viewY, viewZ;
	
	ChunkRenderManager(WorldRenderer renderer) {
		
		this.renderer = renderer;
		
		this.chunkRenderers = new HashMap<>();
		this.unloadingChunkRenderers = new ArrayList<>();
		
		this.layerHandlers = new ChunkLayerDataProvider[BlockRenderLayer.COUNT];
		this.setLayerHandler(BlockRenderLayer.OPAQUE, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT, ChunkSortedLayerData::new);
		this.setLayerHandler(BlockRenderLayer.TRANSPARENT, ChunkSortedLayerData::new);
		
		this.chunkComputer = Executors.newFixedThreadPool(2);
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.renderer;
	}
	
	private void setLayerHandler(BlockRenderLayer layer, ChunkLayerDataProvider handler) {
		this.layerHandlers[layer.ordinal()] = handler;
	}
	
	public ChunkLayerData provideLayerData(BlockRenderLayer layer, Chunk chunk) {
		return this.layerHandlers[layer.ordinal()].provide(chunk, layer);
	}
	
	void render(float alpha) {
		
		this.chunkRenderers.forEach((pos, cr) -> {
			
			// cr.checkLastNeighbours();
			cr.render(RENDER_DISTANCE_SQUARED);
			
		});
	
	}
	
	void update() {
	
		
	
	}
	
	void unload() {
		
		this.chunkRenderers.values().forEach(ChunkRenderer::delete);
		this.chunkRenderers.clear();
		
	}
	
	private void loadChunkRenderer(Chunk chunk) {
		
		BlockPosition pos = chunk.getChunkPosition();
		ChunkRenderer cr = this.chunkRenderers.get(pos);
		
		if (cr == null) {
			
			cr = new ChunkRenderer(this, chunk);
			this.chunkRenderers.put(pos, cr);
			
			cr.init();
			
		}
		
	}
	
	void updateViewPosition(Camera3D cam) {
		this.updateViewPosition(cam.getX(), cam.getY(), cam.getZ());
	}
	
	void updateViewPosition(float x, float y, float z) {
		
		this.viewX = x;
		this.viewY = y;
		this.viewZ = z;
		
		this.chunkRenderers.forEach((pos, cr) -> {
			
			if (cr.updateDistanceToCamera(x, y, z) > UNLOAD_DISTANCE_SQUARED)
				this.unloadingChunkRenderers.add(pos);
			
		});
		
		if (!this.unloadingChunkRenderers.isEmpty()) {
		
			for (BlockPosition pos : this.unloadingChunkRenderers)
				this.chunkRenderers.remove(pos).delete();
			
			this.unloadingChunkRenderers.clear();
			
		}
		
		World world = this.renderer.getRenderingWorld();
		
		if (world != null) {
			
			world.forEachChunkNear(x, y, z, RENDER_DISTANCE, chunk -> {
			
				if (chunk.getDistanceSquaredTo(x, y, z) <= RENDER_DISTANCE_SQUARED)
					this.loadChunkRenderer(chunk);
			
			});
			
		}
		
	}
	
	void chunkLoaded(Chunk chunk) {
		
		if (chunk.getDistanceSquaredTo(this.viewX, this.viewY, this.viewZ) <= RENDER_DISTANCE_SQUARED)
			this.loadChunkRenderer(chunk);
		
	}
	
	void chunkUnloaded(Chunk chunk) {
	
		ChunkRenderer cr = this.chunkRenderers.get(chunk.getChunkPosition());
		
		if (cr != null) {
		
			this.chunkRenderers.remove(chunk.getChunkPosition());
			cr.delete();
		
		}
	
	}
	
}
