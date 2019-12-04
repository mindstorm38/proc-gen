package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ChunkRenderer implements WorldChunkUpdatedListener {
	
	private final ChunkRenderManager renderManager;
	private final WorldRenderer renderer;
	private final Chunk chunk;
	private final World world;
	private final TextureMap terrainMap;
	
	private final Map<Integer, ChunkRenderer> neighbours;
	private final boolean[] lastNeighbours;
	
	private final ChunkLayerData[] layers;
	private final IndicesDrawBuffer[] drawBuffers;
	
	private float distanceToCameraSquared;
	
	ChunkRenderer(ChunkRenderManager renderManager, Chunk chunk) {
		
		this.renderManager = renderManager;
		this.renderer = renderManager.getWorldRenderer();
		this.chunk = chunk;
		this.world = chunk.getWorld();
		this.terrainMap = renderer.getTerrainMap();
		
		this.neighbours = new HashMap<>();
		this.lastNeighbours = new boolean[Direction.values().length];
		
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
			this.drawBuffers[i] = this.renderer.getShaderManager().createBasicDrawBuffer(false, true);
	
		this.chunk.addUpdatedListener(this);
		
	}
	
	void delete() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].delete();
		
		this.chunk.removeUpdatedListener(this);
		
	}
	
	/*
	void setNeighbour(Direction face, ChunkRenderer cr) {
		this.neighbours.put(face.ordinal(), cr);
	}
	
	void removeNeighbour(Direction face) {
		this.neighbours.remove(face.ordinal());
	}
	
	void checkLastNeighbours() {
		
		boolean neighbour;
		boolean refresh = false;
		
		for (int i = 0; i < Direction.values().length; i++) {
			
			neighbour = this.neighbours.containsKey(i);
			
			if (!refresh && neighbour != this.lastNeighbours[i])
				refresh = true;
			
			this.lastNeighbours[i] = neighbour;
			
		}
		
		if (refresh)
			this.refreshBuffers();
		
	}
	*/
	
	float updateDistanceToCamera(float x, float y, float z) {
		return this.distanceToCameraSquared = this.chunk.getDistanceSquaredTo(x, y, z);
	}
	
	float getDistanceToCameraSquared() {
		return this.distanceToCameraSquared;
	}
	
	void render(float maxdist) {
		
		if (this.distanceToCameraSquared <= maxdist)
			render();
		
	}
	
	void render() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].drawElements();
		
	}
	
	private void uploadLayerData(BlockRenderLayer layer) {
		
		ChunkLayerData layerData = this.getLayerData(layer);
		
		FloatBuffer verticesBuf = null;
		FloatBuffer texcoordsBuf = null;
		IntBuffer indicesBuf = null;
		
		try {
			
			verticesBuf = MemoryUtil.memAllocFloat(layerData.getVertices().getSize());
			texcoordsBuf = MemoryUtil.memAllocFloat(layerData.getTexcoords().getSize());
			indicesBuf = MemoryUtil.memAllocInt(this.drawBuffers[0].setIndicesCount(layerData.getIndices().getSize()));
			
			verticesBuf.put(layerData.getVertices().result());
			texcoordsBuf.put(layerData.getTexcoords().result());
			indicesBuf.put(layerData.getIndices().result());
			
			verticesBuf.flip();
			texcoordsBuf.flip();
			indicesBuf.flip();
			
			IndicesDrawBuffer drawBuffer = this.getDrawBuffer(layer);
			
			drawBuffer.bindVao();
			drawBuffer.uploadVboData(BasicFormat.BASIC3D_POSITION, verticesBuf, BufferUsage.DYNAMIC_DRAW);
			drawBuffer.uploadVboData(BasicFormat.BASIC_TEX_COORD, texcoordsBuf, BufferUsage.DYNAMIC_DRAW);
			drawBuffer.uploadIboData(indicesBuf, BufferUsage.DYNAMIC_DRAW);
			
		} finally {
			
			BufferUtils.safeFree(verticesBuf);
			BufferUtils.safeFree(texcoordsBuf);
			BufferUtils.safeFree(indicesBuf);
			
		}
	
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk, int x, int y, int z, Block block) {
		
		if (this.chunk == chunk) {
			
			this.getLayerData(block.getRenderLayer()).handleChunkUpdate(this.renderer);
			this.uploadLayerData(block.getRenderLayer());
			
		}
		
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk) {
		
		if (this.chunk == chunk) {
			
			for (ChunkLayerData layerData : this.layers) {
				
				layerData.handleChunkUpdate(this.renderer);
				this.uploadLayerData(layerData.getLayer());
				
			}
			
		}
		
	}
	
}
