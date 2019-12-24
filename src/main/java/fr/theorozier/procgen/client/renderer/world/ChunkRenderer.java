package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.layer.ChunkLayerData;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChunkRenderer implements Comparable<ChunkRenderer> {
	
	private final ChunkRenderManager renderManager;
	private final WorldRenderer renderer;
	private final WorldChunk chunk;
	
	private final Map<Integer, ChunkRenderer> neighbours;
	
	private final ChunkLayerData[] layers;
	private final IndicesDrawBuffer[] drawBuffers;
	
	private int distanceToCameraSquared;
	
	ChunkRenderer(ChunkRenderManager renderManager, WorldChunk chunk) {
		
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
	
	public ImmutableBlockPosition getChunkPosition() {
		return this.chunk.getChunkPos();
	}
	
	void init() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i] = this.renderer.getShaderManager().createBasicDrawBuffer(true, true);
		
		this.setNeedUpdate(true);
		
	}
	
	void delete() {
		
		this.neighbours.forEach((i, cr) -> cr.removeNeighbour(Direction.values()[i].oposite()));
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].delete();
		
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
		return this.distanceToCameraSquared = (int) this.chunk.getDistSquaredTo(x, y, z);
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
	
	void chunkUpdated(WorldChunk chunk) {
		
		if (this.chunk == chunk) {
			
			this.neighbours.forEach((i, cr) -> cr.setNeedUpdate(true));
			this.setNeedUpdate(true);
			
		}
		
	}
	
	void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState block) {
		
		if (this.chunk == chunk) {
			
			checkBlockOnFaces(pos, dir -> {
				
				ChunkRenderer neighbour = this.neighbours.get(dir.ordinal());
				
				if (neighbour != null)
					neighbour.setNeedUpdate(true);
				
			});
			
			this.setNeedUpdate(block.getBlock().getRenderLayer(), true);
			
		}
		
	}
	
	private static void checkBlockOnFaces(BlockPositioned pos, Consumer<Direction> consumer) {
		
		int rx = pos.getX() & 15;
		int ry = pos.getY() & 15;
		int rz = pos.getZ() & 15;
		
		if (rx == 0) consumer.accept(Direction.SOUTH);
		else if (rx == 15) consumer.accept(Direction.NORTH);
		
		if (ry == 0) consumer.accept(Direction.BOTTOM);
		else if (ry == 15) consumer.accept(Direction.TOP);
		
		if (rz == 0) consumer.accept(Direction.WEST);
		else if (rz == 15) consumer.accept(Direction.EAST);
		
	}
	
	@Override
	public int compareTo(ChunkRenderer o) {
		return o.distanceToCameraSquared - this.distanceToCameraSquared;
	}
	
	@Override
	public int hashCode() {
		return this.chunk.getChunkPos().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj.getClass() != getClass()) return true;
		ChunkRenderer render = (ChunkRenderer) obj;
		return this.chunk.getChunkPos().equals(render.getChunkPosition());
	}
	
}
