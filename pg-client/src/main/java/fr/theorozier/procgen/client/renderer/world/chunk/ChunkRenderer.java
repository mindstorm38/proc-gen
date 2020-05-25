package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.client.renderer.block.BlockRenderers;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.client.renderer.world.util.WorldSequentialFormat;
import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldRenderDataBuffer;
import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldSequentialBuffer;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.util.BufferUsage;
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
	
	private static final int LAYERS_COUNT = BlockRenderLayer.COUNT;
	private static final int CHANGED_ALL = (1 << LAYERS_COUNT) - 1;
	
	private final ChunkRenderManager renderManager;
	private final WorldRenderer renderer;
	private final ModelHandler model;
	
	private final ChunkRenderer[] neighbours = new ChunkRenderer[Direction.COUNT];
	
	//private final ChunkLayerData[] layers = new ChunkLayerData[LAYERS_COUNT];
	private final IndicesDrawBuffer[] drawBuffers = new IndicesDrawBuffer[LAYERS_COUNT];
	
	/*
	 * Shapes cache for each blocks, blocks are indexed as they are in {@link WorldChunk}.
	 */
	//private final int[] shapesCache = new int[4096];
	//private final int[] shapesVertCounts = new int[LAYERS_COUNT];
	//private final FixedShortStack changedBlocks = new FixedShortStack(512);
	//private final FixedShortStack redrawingBlocks = new FixedShortStack(512);
	//private boolean changedGlobal = false;
	private boolean redrawing = false;
	private int changed = 0;
	
	private WorldChunk chunk = null;
	private int distanceToCameraSquared = 0;
	//private int firstUpdates = 0;
	private boolean firstUpdated = false;
	private int chunkX = 0;
	private int chunkZ = 0;
	
	public ChunkRenderer(ChunkRenderManager renderManager) {
		
		this.renderManager = renderManager;
		this.renderer = renderManager.getWorldRenderer();
		this.model = renderManager.getWorldRenderer().getModelHandler();
		
		//this.setupLayers();
		
	}
	
	/*
	 * Internal method to setup layer
	 */
	/*private void setupLayers() {
		
		for (BlockRenderLayer layer : BlockRenderLayer.values())
			this.layers[layer.ordinal()] = this.renderManager.provideLayerData(layer);
		
	}
	
	private ChunkLayerData getLayerData(BlockRenderLayer layer) {
		return this.layers[layer.ordinal()];
	}*/
	
	private IndicesDrawBuffer getDrawBuffer(BlockRenderLayer layer) {
		return this.drawBuffers[layer.ordinal()];
	}
	
	public void init() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i] = this.renderer.getShaderManager().createSequentialDrawBuffer();
		
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
		//this.firstUpdates = 0;
		this.firstUpdated = false;
		
		this.chunkX = chunk.getChunkPos().getX() << 4;
		this.chunkZ = chunk.getChunkPos().getZ() << 4;
		
		// this.roX = (chunk.getChunkPos().getX() & 15) << 4;
		// this.roZ = (chunk.getChunkPos().getZ() & 15) << 4;
		
		//for (ChunkLayerData layerData : this.layers)
		//	layerData.setChunk(chunk/*, this.roX, this.roZ*/);
		
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
	
	public boolean isRenderable() {
		return this.isActive() && this.firstUpdated;
	}
	
	public WorldChunk getChunk() {
		return this.chunk;
	}
	
	public ImmutableBlockPosition getChunkPosition() {
		return this.chunk.getChunkPos();
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
	
	public void setNeedUpdate(BlockRenderLayer layer) {
		//this.getLayerData(layer).setNeedUpdate(needUpdate);
		this.changed |= 1 << layer.ordinal();
	}
	
	public void setNeedUpdate() {
		//for (ChunkLayerData layerData : this.layers)
		//	layerData.setNeedUpdate(needUpdate);
		this.changed = CHANGED_ALL;
	}
	
	public boolean doNeedUpdate(BlockRenderLayer layer) {
		int bit = 1 << layer.ordinal();
		return (this.changed & bit) == bit;
	}
	
	public boolean doNeedUpdate() {
		return this.changed != 0;
	}
	
	public void update() {
		
		if (this.isActive()) {
			
			if (!this.redrawing && this.doNeedUpdate()) {
				
				this.redrawing = true;
				this.renderManager.scheduleChunkRedrawTask(this, this::redrawGlobal);
				
				/*if (this.changedGlobal) {
					this.redrawing = true;
					this.changedGlobal = false;
					this.changedBlocks.clear();
					this.renderManager.scheduleChunkRedrawTask(this, this::redrawGlobal);
				} else if (this.changedBlocks.hasAny()) {
					this.redrawing = true;
					this.changedBlocks.copyTo(this.redrawingBlocks);
					this.changedBlocks.clear();
					this.renderManager.scheduleChunkRedrawTask(this, this::redrawBlocks);
				}*/
				
			}
			
			/* for (ChunkLayerData layerData : this.layers) {
				if (layerData.doNeedUpdate()) {
					
					layerData.handleChunkUpdate(this);
					layerData.setNeedUpdate(false);
					
				}
			}*/
			
		}
		
	}
	
	// RENDER //
	
	/**
	 * <p>Render a specific block render layer only if the internal computed squared distance to camera
	 * (using {@link #updateDistanceToCamera(float, float, float)}) is less or equals than (squared) 'maxdist'
	 * <b>and</b> this chunk is active.</p>
	 * @param layer The render layer to render.
	 * @param maxdist The maximum (squared) distance.
	 * @see #render(BlockRenderLayer, float, float)
	 */
	public void render(BlockRenderLayer layer, int maxdist, float camX, float camZ) {
		
		if (this.isRenderable() && this.distanceToCameraSquared <= maxdist)
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
		
		/* for (ChunkLayerData layerData : this.layers) {
			layerData.handleNewViewPosition(this, x, y, z);
		} */
		
	}
	
	/*public void chunkUpdateDone(BlockRenderLayer layer) {
		
		ChunkLayerData data = this.getLayerData(layer);
		
		if (data != null) {
			
			this.uploadLayerData(data);
			
			if (!this.firstUpdated) {
				
				this.firstUpdates |= 1 << layer.ordinal();
				this.firstUpdated = (this.firstUpdates & FIRST_UPDATED_MASK) == FIRST_UPDATED_MASK;
				
			}
			
		}
		
	}*/
	
	/*private void uploadLayerData(ChunkLayerData layerData) {
		
		IndicesDrawBuffer drawBuffer = this.getDrawBuffer(layerData.getLayer());
		layerData.getDataArray().uploadToDrawBuffer(drawBuffer);
	
	}*/
	
	public void chunkUpdated(WorldChunk chunk) {
		
		if (this.chunk == chunk) {
			
			/* for (int i = 0; i < Direction.COUNT; ++i)
				this.neighbours[i].setNeedUpdate(true);
			
			this.setNeedUpdate(true);
			this.changedGlobal = true;*/
			
			this.setNeedUpdate();
			
		}
		
	}
	
	public void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState block, BlockState previousState) {
		
		if (this.chunk == chunk) {
			
			this.setNeedUpdate(block.getBlock().getRenderLayer());
			this.setNeedUpdate(previousState.getBlock().getRenderLayer());
			
			//this.changedGlobal = true;
			/*if (!this.changedGlobal && !this.changedBlocks.push(getBlockLayeredIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, previousState.getBlock().getRenderLayer()))) {
				this.changedGlobal = true;
			}*/
			
			/*
			checkBlockOnFaces(pos, dir -> {
				
				ChunkRenderer neighbour = this.neighbours[dir.ordinal()];
				
				if (neighbour != null)
					neighbour.setNeedUpdate(true);
				
			});
			
			this.setNeedUpdate(block.getBlock().getRenderLayer(), true);
			*/
			
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
	
	// BUFFER UPDATES //
	
	private void redrawGlobal(WorldChunk chunk, ChunkUploadDescriptor uploadDescriptor) {
		
		// Arrays.fill(this.shapesVertCounts, 0);
		
		/*for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			uploadDescriptor.getLayerBufferObjectCreate(layer, this.renderManager.getSequentialBufferPool());
		}*/
		
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			if (this.doNeedUpdate(layer)) {
				uploadDescriptor.clearBuffer(layer, this.renderManager.getSequentialBufferPool());
			}
		}
		
		TextureMap terrainMap = this.renderer.getTerrainMap();
		BlockFaces faces = new BlockFaces();
		BlockRenderer renderer;
		BlockState state;
		WorldRenderDataBuffer buffer;
		
		WorldBase world = chunk.getWorld();
		int cx = chunk.getChunkPos().getX() << 4;
		int cy = chunk.getChunkPos().getY() << 4;
		int cz = chunk.getChunkPos().getZ() << 4;
		
		//int shapeCache;
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					
					//shapeCache = 0;
					
					if ((state = chunk.getBlockAt(x, y, z)) != null && this.doNeedUpdate(state.getBlock().getRenderLayer())) {
						if ((renderer = BlockRenderers.getRenderer(state.getBlock())) != null) {
							
							if (renderer.needFaces()) {
								computeBlockFaces(faces, state, chunk, x, y, z);
							} else {
								faces.setVisible();
							}
							
							if (faces.isVisible()) {
								
								//buffer = uploadDescriptor.getLayerBufferObject(state.getBlock().getRenderLayer()).get();
								
								//int vertices = buffer.vertices();
								//int layer = state.getBlock().getRenderLayer().ordinal();
								buffer = uploadDescriptor.getBuffer(state.getBlock().getRenderLayer());
								renderer.getRenderData(world, state, cx + x, cy + y, cz + z, x, y, z, faces, terrainMap, buffer);
								//vertices = buffer.vertices() - vertices;
								//shapeCache = buildShapeCache(this.shapesVertCounts[layer], vertices);
								//this.shapesVertCounts[layer] += vertices;
								
							}
							
						}
					}
					
					//this.shapesCache[WorldChunk.getBlockIndex(x, y, z)] = shapeCache;
					
				}
			}
		}
		
		/*for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			uploadDescriptor.addUploadSegmentAll(layer);
		}*/
		
	}
	
	/*private void redrawBlocks(WorldChunk chunk, ChunkUploadDescriptor uploadDescriptor) {
		
		BlockFaces faces = new BlockFaces();
		TextureMap terrainMap = this.renderer.getTerrainMap();
		
		WorldBase world = chunk.getWorld();
		int cx = chunk.getChunkPos().getX() << 4;
		int cy = chunk.getChunkPos().getY() << 4;
		int cz = chunk.getChunkPos().getZ() << 4;
		
		this.redrawingBlocks.forEach(idx -> {
			
			BlockState state = chunk.getBlockAtIndex(idx);
			BlockRenderLayer previousLayer = getBlockLayer(idx);
			int previousLayerIdx = previousLayer.ordinal();
			
			if (state != null) {
				BlockRenderer renderer = BlockRenderers.getRenderer(state.getBlock());
				if (renderer != null) {
					
					int x = WorldChunk.getBlockX(idx);
					int y = WorldChunk.getBlockY(idx);
					int z = WorldChunk.getBlockZ(idx);
					
					if (renderer.needFaces()) {
						computeBlockFaces(faces, state, chunk, x, y, z);
					} else {
						faces.setVisible();
					}
					
					if (faces.isVisible()) {
						
						BlockRenderLayer layer = state.getBlock().getRenderLayer();
						int layerIdx = layer.ordinal();
						
						WorldRenderDataBuffer buffer = uploadDescriptor.getLayerBufferObjectCreate(layer, this.renderManager.getSequentialBufferPool()).get();
						
						int lastVerticesOffset = buffer.vertices();
						renderer.getRenderData(world, state, cx + x, cy + y, cz + z, x, y, z, faces, terrainMap, buffer);
						int verticesCount = buffer.vertices() - lastVerticesOffset;
						
						if (verticesCount > 0) {
							
							int shapeCache = this.shapesCache[idx];
							int shapeCount = getShapeVerticesCount(shapeCache);
							int shapeOffset;
							
							if (previousLayerIdx == layerIdx) {
								
								shapeOffset = (shapeCount == 0) ? this.shapesVertCounts[layerIdx] : getShapeVerticesOffset(shapeCache);
								uploadDescriptor.addUploadSegment(layer, lastVerticesOffset, verticesCount, shapeOffset, shapeCount);
								
							} else {
								
								this.shapesVertCounts[previousLayerIdx] -= shapeCount;
								shapeOffset = this.shapesVertCounts[layerIdx];
								
								if (shapeCount != 0) {
									uploadDescriptor.addUploadSegment(previousLayer, 0, 0, getShapeVerticesOffset(shapeCache), shapeCount);
								}
								
								uploadDescriptor.addUploadSegment(layer, lastVerticesOffset, verticesCount, shapeOffset, 0);
								
							}
							
							this.shapesCache[idx] = buildShapeCache(shapeOffset, verticesCount);
							return;
							
						}
						
					}
					
				}
			}
			
			// SPLICE BLOCK
			
			// uploadDescriptor.addUploadSegment(previousLayer, );
			
		});
		
	}*/
	
	/**
	 * Must be called from main thread to upload a vertices buffer computer in parallel
	 * to GL buffers.
	 * @param uploadDescriptor The upload descriptor written in parallel.
	 */
	public void uploadComputedDescription(ChunkUploadDescriptor uploadDescriptor) {
		
		WorldSequentialBuffer buff;
		for (int i = 0, bit; i < LAYERS_COUNT; ++i) {
			bit = 1 << i;
			if ((this.changed & bit) == bit) {
				if ((buff = uploadDescriptor.getBuffer(i)) != null) {
					buff.flip();
					this.drawBuffers[i].uploadVboData(WorldSequentialFormat.SEQUENTIAL_MAIN, buff.getData(), BufferUsage.DYNAMIC_DRAW);
					this.drawBuffers[i].uploadIboData(buff.getIndices(), BufferUsage.DYNAMIC_DRAW);
				}
			}
		}
		
		this.redrawing = false;
		this.changed = 0;
		
		/*uploadDescriptor.forEachUploadSegment((layer, buff, from, fromLen, to, toReplaceLen) -> {
			
			IndicesDrawBuffer drawBuffer = this.drawBuffers[layer.ordinal()];
			buff.position(from);
			buff.limit(from + fromLen);
			
		});
		
		this.redrawing = false;
		this.firstUpdated = true;*/
		
	}
	
	// OBJECTS METHODS //
	
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
	
	private static int buildShapeCache(int verticesOffset, int verticesCount) {
		return (verticesCount & 1023) | (verticesOffset << 10);
	}
	
	private static int getShapeVerticesOffset(int shapeCache) {
		return shapeCache >> 10;
	}
	
	private static int getShapeVerticesCount(int shapeCache) {
		return shapeCache & 1023;
	}
	
	private static void computeBlockFaces(BlockFaces faces, BlockState state, WorldChunk chunk, int x, int y, int z) {
		for (Direction direction : Direction.values()) {
			faces.setFaceBlock(state, direction, chunk.getBlockAtBlockRel(x, y, z, direction));
		}
	}
	
	private static short getBlockLayeredIndex(int x, int y, int z, BlockRenderLayer layer) {
		return (short) (WorldChunk.getBlockIndex(x, y, z) | (layer.ordinal() << 12));
	}
	
	private static BlockRenderLayer getBlockLayer(short layeredIndex) {
		return BlockRenderLayer.values()[(layeredIndex >> 12) & 15];
	}
	
}
