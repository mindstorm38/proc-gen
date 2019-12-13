package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ChunkRenderer implements Comparable<ChunkRenderer>, WorldChunkUpdatedListener {
	
	private final ChunkRenderManager renderManager;
	private final WorldRenderer renderer;
	private final Chunk chunk;
	
	private final Map<Integer, ChunkRenderer> neighbours;
	
	private final ChunkLayerData[] layers;
	private final IndicesDrawBuffer[] drawBuffers;
	
	private int distanceToCameraSquared;
	
	ChunkRenderer(ChunkRenderManager renderManager, Chunk chunk) {
		
		this.renderManager = renderManager;
		this.renderer = renderManager.getWorldRenderer();
		this.chunk = chunk;
		
		this.neighbours = new HashMap<>();
		
		this.layers = new ChunkLayerData[BlockRenderLayer.COUNT];
		this.drawBuffers = new IndicesDrawBuffer[BlockRenderLayer.COUNT];
		
		this.setupLayers();
		
		this.distanceToCameraSquared = 0;
		
	}
	
	private void setupLayers() {
		
		for (BlockRenderLayer layer : BlockRenderLayer.values())
			this.layers[layer.ordinal()] = this.renderManager.provideLayerData(layer, this.chunk);
		
	}
	
	private ChunkLayerData getLayerData(BlockRenderLayer layer) {
		return this.layers[layer.ordinal()];
	}
	
	private IndicesDrawBuffer getDrawBuffer(BlockRenderLayer layer) {
		return this.drawBuffers[layer.ordinal()];
	}
	
	public BlockPosition getChunkPosition() {
		return this.chunk.getChunkPosition();
	}
	
	void init() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i] = this.renderer.getShaderManager().createBasicDrawBuffer(true, true);
		
		this.setNeedUpdate(true);
		this.chunk.addUpdatedListener(this);
		
	}
	
	void delete() {
		
		this.neighbours.forEach((i, cr) -> cr.removeNeighbour(Direction.values()[i].oposite()));
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].delete();
		
		this.chunk.removeUpdatedListener(this);
		
	}
	
	void setNeighbour(Direction face, ChunkRenderer cr) {
		this.neighbours.put(face.ordinal(), cr);
	}
	
	void removeNeighbour(Direction face) {
		this.neighbours.remove(face.ordinal());
	}
	
	void setNeedUpdate(BlockRenderLayer layer, boolean needUpdate) {
		this.getLayerData(layer).setNeedUpdate(needUpdate);
	}
	
	void setNeedUpdate(boolean needUpdate) {
		
		for (ChunkLayerData layerData : this.layers)
			layerData.setNeedUpdate(needUpdate);
		
	}
	
	void render(BlockRenderLayer layer, int maxdist) {
		
		if (this.distanceToCameraSquared <= maxdist)
			this.render(layer);
		
	}
	
	void render(BlockRenderLayer layer) {
		this.drawBuffers[layer.ordinal()].drawElements();
	}
	
	void update() {
		
		for (ChunkLayerData layerData : this.layers) {
			if (layerData.doNeedUpdate()) {
				
				layerData.handleChunkUpdate(this);
				layerData.setNeedUpdate(false);
				
			}
		}
	
	}
	
	float updateDistanceToCamera(float x, float y, float z) {
		return this.distanceToCameraSquared = (int) this.chunk.getDistanceSquaredTo(x, y, z);
	}
	
	void updateViewPosition(int x, int y, int z) {
		
		for (ChunkLayerData layerData : this.layers) {
			layerData.handleNewViewPosition(this, x, y, z);
		}
		
	}
	
	void chunkUpdateDone(BlockRenderLayer layer) {
		
		ChunkLayerData data = this.getLayerData(layer);
		if (data != null) this.uploadLayerData(data);
		
	}
	
	private void uploadLayerData(ChunkLayerData layerData) {
		
		IndicesDrawBuffer drawBuffer = this.getDrawBuffer(layerData.getLayer());
		layerData.getDataArray().uploadToDrawBuffer(drawBuffer);
	
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk, int x, int y, int z, Block block) {
		
		if (this.chunk == chunk) {
			
			chunk.checkBlockOnFaces(x, y, z, dir -> {
				
				ChunkRenderer neighbour = this.neighbours.get(dir.ordinal());
				
				if (neighbour != null)
					neighbour.setNeedUpdate(true);
				
			});
			
			this.setNeedUpdate(block.getRenderLayer(), true);
			
			
		}
		
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk) {
		
		if (this.chunk == chunk) {
			
			this.neighbours.forEach((i, cr) -> cr.setNeedUpdate(true));
			this.setNeedUpdate(true);
			
		}
		
	}
	
	@Override
	public int compareTo(ChunkRenderer o) {
		return o.distanceToCameraSquared - this.distanceToCameraSquared;
	}
	
	@Override
	public int hashCode() {
		return this.chunk.getChunkPosition().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj.getClass() != getClass()) return true;
		ChunkRenderer render = (ChunkRenderer) obj;
		return this.chunk.getChunkPosition().equals(render.getChunkPosition());
	}
	
}
