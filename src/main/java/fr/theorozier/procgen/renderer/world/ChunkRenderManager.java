package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.layer.ChunkDirectLayerData;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerDataProvider;
import fr.theorozier.procgen.renderer.world.layer.ChunkSortedLayerData;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.util.camera.Camera3D;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkRenderManager {
	
	// These distances are squared, for optimisation.
	public static final int RENDER_DISTANCE = 16 * 12;
	public static final int UNLOAD_DISTANCE = 16 * 16;
	
	public static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;
	public static final int UNLOAD_DISTANCE_SQUARED = UNLOAD_DISTANCE * UNLOAD_DISTANCE;
	
	private final WorldRenderer renderer;
	
	private final Map<BlockPosition, ChunkRenderer> chunkRenderers;
	private final List<ChunkRenderer> chunkRenderersList;
	private final List<BlockPosition> unloadingChunkRenderers;
	
	private final ChunkLayerDataProvider[] layerHandlers;
	
	private final ExecutorService chunkComputer;
	
	private float viewX, viewY, viewZ;
	
	ChunkRenderManager(WorldRenderer renderer) {
		
		this.renderer = renderer;
		
		this.chunkRenderers = new HashMap<>();
		this.chunkRenderersList = new ArrayList<>();
		this.unloadingChunkRenderers = new ArrayList<>();
		
		this.layerHandlers = new ChunkLayerDataProvider[BlockRenderLayer.COUNT];
		this.setLayerHandler(BlockRenderLayer.OPAQUE, ChunkDirectLayerData::new);
		this.setLayerHandler(BlockRenderLayer.CUTOUT, ChunkDirectLayerData::new);
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
	
	private void resortChunkRenderers() {
		this.chunkRenderersList.sort(ChunkRenderer::compareTo);
	}
	
	void render(BlockRenderLayer layer) {
		this.chunkRenderersList.forEach(cr -> cr.render(layer, RENDER_DISTANCE_SQUARED));
	}
	
	void update() {
		this.chunkRenderersList.forEach(ChunkRenderer::update);
	}
	
	void unload() {
		
		this.chunkRenderersList.forEach(ChunkRenderer::delete);
		this.chunkRenderersList.clear();
		this.chunkRenderers.clear();
		
	}
	
	private void loadChunkRenderer(Chunk chunk) {
		
		BlockPosition pos = chunk.getChunkPosition();
		ChunkRenderer cr = this.chunkRenderers.get(pos);
		
		if (cr == null) {
			
			//System.out.println("Loading chunk at " + pos);
			
			cr = new ChunkRenderer(this, chunk);
			this.chunkRenderers.put(pos, cr);
			this.chunkRenderersList.add(cr);
			
			this.resortChunkRenderers();
			
			cr.init();
			
			ChunkRenderer neighbour;
			for (Direction dir : Direction.values()) {
				if ((neighbour = this.chunkRenderers.get(pos.add(dir, 16, 16, 16))) != null) {
					
					neighbour.setNeedUpdate(true);
					
					neighbour.setNeighbour(dir.oposite(), cr);
					cr.setNeighbour(dir, neighbour);
					
				}
			}
			
		}
		
	}
	
	private void deleteChunkRenderer(BlockPosition pos) {
		
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
				this.unloadingChunkRenderers.add(pos);
			} else {
				cr.updateViewPosition(ix, iy, iz);
			}
			
		});
		
		if (!this.unloadingChunkRenderers.isEmpty()) {
			
			this.unloadingChunkRenderers.forEach(this::deleteChunkRenderer);
			this.unloadingChunkRenderers.clear();
			
		}
		
		this.resortChunkRenderers();
		
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
		this.deleteChunkRenderer(chunk.getChunkPosition());
	}
	
}
