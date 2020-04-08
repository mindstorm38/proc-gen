package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.WorldCamera;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.chunk.layer.ChunkLayerData;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * <p>ChunkRenderer manage a single vertical chunk render data.</p>
 * <p>It store its neighbours, layers and draw buffers ({@link IndicesDrawBuffer}).</p>
 * <p>Internal rendered chunks can be dynamically changed and re-rendered.</p>
 *
 * @author Théo Rozier
 *
 */
public class ChunkRenderer implements Comparable<ChunkRenderer> {
	
	private final ChunkRenderManager renderManager;
	private final WorldRenderer renderer;
	private final ModelHandler model;
	private final WorldCamera camera;
	
	private final ChunkRenderer[] neighbours;
	
	private final ChunkLayerData[] layers;
	private final IndicesDrawBuffer[] drawBuffers;
	
	private WorldChunk chunk = null;
	private int distanceToCameraSquared = 0;
	
	// Render Offsets
	private int roX = 0;
	private int roZ = 0;
	private int chunkX = 0;
	private int chunkZ = 0;
	
	public ChunkRenderer(ChunkRenderManager renderManager) {
		
		this.renderManager = renderManager;
		this.renderer = renderManager.getWorldRenderer();
		this.model = renderManager.getWorldRenderer().getModelHandler();
		this.camera = renderManager.getWorldRenderer().getCamera();
		
		this.neighbours = new ChunkRenderer[Direction.COUNT];
		
		this.layers = new ChunkLayerData[BlockRenderLayer.COUNT];
		this.drawBuffers = new IndicesDrawBuffer[BlockRenderLayer.COUNT];
		
		this.setupLayers();
		
	}
	
	/**
	 * Internal method to setup layer
	 */
	private void setupLayers() {
		
		for (BlockRenderLayer layer : BlockRenderLayer.values())
			this.layers[layer.ordinal()] = this.renderManager.provideLayerData(layer);
		
	}
	
	private ChunkLayerData getLayerData(BlockRenderLayer layer) {
		return this.layers[layer.ordinal()];
	}
	
	private IndicesDrawBuffer getDrawBuffer(BlockRenderLayer layer) {
		return this.drawBuffers[layer.ordinal()];
	}
	
	public void init() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i] = this.renderer.getShaderManager().createBasicDrawBuffer(true, true);
		
	}
	
	public void delete() {
		
		for (int i = 0; i < Direction.COUNT; ++i)
			if (this.neighbours[i] != null)
				this.neighbours[i].removeNeighbour(Direction.values()[i].oposite());
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].delete();
		
	}
	
	public void setChunk(WorldChunk chunk) {
		
		this.chunk = Objects.requireNonNull(chunk, "ChunkRenderer chunk can't be null.");
		
		this.chunkX = chunk.getChunkPos().getX() << 4;
		this.chunkZ = chunk.getChunkPos().getZ() << 4;
		
		this.roX = (chunk.getChunkPos().getX() & 15) << 4;
		this.roZ = (chunk.getChunkPos().getZ() & 15) << 4;
		
		for (ChunkLayerData layerData : this.layers)
			layerData.setChunk(chunk, this.roX, this.roZ);
		
	}
	
	public void releaseChunk() {
		this.chunk = null;
	}
	
	/**
	 * @return True if the {@link ChunkRenderer} currently handle a Chunk for rendering.
	 */
	public boolean isActive() {
		return this.chunk != null;
	}
	
	public WorldChunk getChunk() {
		return this.chunk;
	}
	
	public ImmutableBlockPosition getChunkPosition() {
		return this.chunk.getChunkPos();
	}
	
	public int getRenderOffsetX() {
		return this.roX;
	}
	
	public int getRenderOffsetZ() {
		return this.roZ;
	}
	
	// NEIGHBOURS //
	
	public void setNeighbour(Direction face, ChunkRenderer cr) {
		this.neighbours[face.ordinal()] = cr;
	}
	
	public void removeNeighbour(Direction face) {
		this.setNeighbour(face, null);
	}
	
	public void removeAllNeighbours(BiConsumer<Direction, ChunkRenderer> consumer) {
		for (Direction dir : Direction.values()) {
			ChunkRenderer nb = this.neighbours[dir.ordinal()];
			if (nb != null) {
				this.neighbours[dir.ordinal()] = null;
				consumer.accept(dir, nb);
			}
		}
	}
	
	// UPDATES //
	
	public void setNeedUpdate(BlockRenderLayer layer, boolean needUpdate) {
		this.getLayerData(layer).setNeedUpdate(needUpdate);
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		
		for (ChunkLayerData layerData : this.layers)
			layerData.setNeedUpdate(needUpdate);
		
	}
	
	public void update() {
		
		if (this.isActive()) {
			for (ChunkLayerData layerData : this.layers) {
				if (layerData.doNeedUpdate()) {
					
					layerData.handleChunkUpdate(this);
					layerData.setNeedUpdate(false);
					
				}
			}
		}
		
	}
	
	/**
	 * <p>Render a specific block render layer only if the internal computed squared distance to camera
	 * (using {@link #updateDistanceToCamera(float, float, float)}) is less or equals than (squared) 'maxdist'
	 * <b>and</b> this chunk is active.</p>
	 * @param layer The render layer to render.
	 * @param maxdist The maximum (squared) distance.
	 * @see #render(BlockRenderLayer, float, float)
	 */
	public void render(BlockRenderLayer layer, int maxdist, float camX, float camZ) {
		
		if (this.isActive() && this.distanceToCameraSquared <= maxdist)
			this.render(layer, camX, camZ);
		
	}
	
	/**
	 * Render a specific block render layer.
	 * @param layer The render layer to render.
	 * @see #render(BlockRenderLayer, int, float, float)
	 */
	public void render(BlockRenderLayer layer, float camX, float camZ) {
		
		this.model.push().translate(this.chunkX - camX, 0, this.chunkZ - camZ).apply();
		this.drawBuffers[layer.ordinal()].drawElements();
		this.model.pop();
		
	}
	
	/**
	 * Update the squared distance to a specific camera position and return it.
	 * @param x The camera X position.
	 * @param y The camera Y position.
	 * @param z The camera Z position.
	 * @return The computed distance to camera.
	 */
	public float updateDistanceToCamera(float x, float y, float z) {
		return this.distanceToCameraSquared = (int) this.chunk.getDistSquaredTo(x, y, z);
	}
	
	public void updateViewPosition(int x, int y, int z) {
		
		for (ChunkLayerData layerData : this.layers) {
			layerData.handleNewViewPosition(this, x, y, z);
		}
		
	}
	
	public void chunkUpdateDone(BlockRenderLayer layer) {
		
		ChunkLayerData data = this.getLayerData(layer);
		if (data != null) this.uploadLayerData(data);
		
	}
	
	private void uploadLayerData(ChunkLayerData layerData) {
		
		IndicesDrawBuffer drawBuffer = this.getDrawBuffer(layerData.getLayer());
		layerData.getDataArray().uploadToDrawBuffer(drawBuffer);
	
	}
	
	public void chunkUpdated(WorldChunk chunk) {
		
		if (this.chunk == chunk) {
			
			for (int i = 0; i < Direction.COUNT; ++i)
				this.neighbours[i].setNeedUpdate(true);
			
			this.setNeedUpdate(true);
			
		}
		
	}
	
	public void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState block) {
		
		if (this.chunk == chunk) {
			
			checkBlockOnFaces(pos, dir -> {
				
				ChunkRenderer neighbour = this.neighbours[dir.ordinal()];
				
				if (neighbour != null)
					neighbour.setNeedUpdate(true);
				
			});
			
			this.setNeedUpdate(block.getBlock().getRenderLayer(), true);
			
		}
		
	}
	
	/**
	 * Utility method to trigger a callback for each direction for neighbors who need updates.
	 * @param pos The block update position.
	 * @param consumer The callback for directions.
	 */
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
		return this.chunk.getChunkPos().hashCode(); // TODO Change that
	}
	
	@Override
	public boolean equals(Object obj) { // TODO Also change this
		if (obj == this) return true;
		if (obj.getClass() != getClass()) return true;
		ChunkRenderer render = (ChunkRenderer) obj;
		return this.chunk.getChunkPos().equals(render.getChunkPosition());
	}
	
}
