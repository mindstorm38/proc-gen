package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.client.renderer.block.BlockRenderers;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.client.renderer.world.chunk.redraw.ChunkRedrawFuture;
import fr.theorozier.procgen.client.renderer.world.util.WorldSequentialFormat;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderSequentialBuffer;
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
	
	private final IndicesDrawBuffer[] drawBuffers = new IndicesDrawBuffer[LAYERS_COUNT];
	
	//private boolean redrawing = false;
	private boolean firstUpdated = false;
	private int changed = 0;
	
	private ChunkRedrawFuture<?> redrawingFuture = null;
	
	private WorldChunk chunk = null;
	private int distanceToCameraSquared = 0;
	private int chunkX = 0;
	private int chunkZ = 0;
	
	public ChunkRenderer(ChunkRenderManager renderManager) {
		
		this.renderManager = renderManager;
		this.renderer = renderManager.getWorldRenderer();
		this.model = renderManager.getWorldRenderer().getModelHandler();
		
	}
	
	private IndicesDrawBuffer getDrawBuffer(BlockRenderLayer layer) {
		return this.drawBuffers[layer.ordinal()];
	}
	
	public void init() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i] = this.renderer.getShaderManager().createSequentialDrawBuffer();
		
	}
	
	public void delete() {
		
		for (int i = 0; i < this.drawBuffers.length; ++i)
			this.drawBuffers[i].delete();
		
	}
	
	public void setChunk(WorldChunk chunk) {
		
		Objects.requireNonNull(chunk, "ChunkRenderer chunk can't be null.");
		
		if (this.chunk != chunk) {
			
			this.chunk = chunk;
			this.firstUpdated = false;
			
			this.chunkX = chunk.getChunkPos().getX() << 4;
			this.chunkZ = chunk.getChunkPos().getZ() << 4;
			
		}
		
		this.setNeedUpdate();
		this.cancelRedrawing();
		
	}
	
	public void releaseChunk() {
		this.chunk = null;
		this.cancelRedrawing();
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
		return this.chunk == null ? null : this.chunk.getChunkPos();
	}
	
	// UPDATES //
	
	public void setNeedUpdate(BlockRenderLayer layer) {
		this.changed |= 1 << layer.ordinal();
	}
	
	public void setNeedUpdate() {
		this.changed = CHANGED_ALL;
	}
	
	public boolean doNeedUpdate(BlockRenderLayer layer) {
		int bit = 1 << layer.ordinal();
		return (this.changed & bit) == bit;
	}
	
	public boolean doNeedUpdate() {
		return this.changed != 0;
	}
	
	public boolean update() {
		
		if (this.isActive() && this.redrawingFuture == null && this.doNeedUpdate()) {
			
			//this.redrawing = true;
			this.redrawingFuture = this.renderManager.scheduleChunkRedrawTask(this, this::redrawGlobal);
			return true;
			
		} else {
			return false;
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
		
		if (this.isRenderable() && this.distanceToCameraSquared <= maxdist) {
			this.render(layer, camX, camZ);
		}
		
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
	
	public float getDistanceToCameraSquared() {
		return this.distanceToCameraSquared;
	}
	
	public void chunkUpdated(WorldChunk chunk) {
		
		if (this.chunk == chunk) {
			this.setNeedUpdate();
		}
		
	}
	
	public void blockUpdated(WorldChunk chunk, BlockPositioned pos, BlockState block, BlockState previousState) {
		
		if (this.chunk == chunk) {
			
			this.setNeedUpdate(block.getBlock().getRenderLayer());
			this.setNeedUpdate(previousState.getBlock().getRenderLayer());
			
			int bx = pos.getX() & 15;
			int by = pos.getY() & 15;
			int bz = pos.getZ() & 15;
			
			BlockState state;
			ChunkRenderer neigh;
			
			for (Direction dir : Direction.values()) {
				
				state = chunk.getBlockAtBlockRel(bx, by, bz, dir);
				
				if (bx == 0 || bx == 15 || by == 0 || by == 15 || bz == 0 || bz == 15) {
					neigh = this.renderManager.getChunkRendererNeighbour(chunk, dir);
				} else {
					neigh = this;
				}
				
				if (state != null && neigh != null) {
					neigh.setNeedUpdate(state.getBlock().getRenderLayer());
				}
				
			}
			
		}
		
	}
	
	// BUFFER REDRAWING //
	
	public boolean isRedrawing() {
		return this.redrawingFuture != null;
	}
	
	private void doneRedrawing() {
		this.redrawingFuture = null;
		this.changed = 0;
	}
	
	public void cancelRedrawing() {
		if (this.redrawingFuture != null) {
			this.redrawingFuture.cancel(false);
			this.redrawingFuture = null;
		}
	}
	
	private void redrawGlobal(WorldChunk chunk, ChunkRenderBuffers renderBuffers) {
		
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			if (this.doNeedUpdate(layer)) {
				renderBuffers.getBuffer(layer).clear();
			}
		}
		
		TextureMap terrainMap = this.renderer.getTerrainMap();
		BlockFaces faces = new BlockFaces();
		BlockRenderer renderer;
		BlockState state;
		WorldRenderBuffer buffer;
		
		WorldBase world = chunk.getWorld();
		int cx = chunk.getChunkPos().getX() << 4;
		int cy = chunk.getChunkPos().getY() << 4;
		int cz = chunk.getChunkPos().getZ() << 4;
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					
					if ((state = chunk.getBlockAt(x, y, z)) != null && this.doNeedUpdate(state.getBlock().getRenderLayer())) {
						if ((renderer = BlockRenderers.getRenderer(state.getBlock())) != null) {
							
							if (renderer.needFaces()) {
								computeBlockFaces(faces, state, chunk, x, y, z);
							} else {
								faces.setVisible();
							}
							
							if (faces.isVisible()) {
								
								buffer = renderBuffers.getBuffer(state.getBlock().getRenderLayer());
								renderer.getRenderData(world, state, cx + x, cy + y, cz + z, x, cy + y, z, faces, terrainMap, buffer);
								
							}
							
						}
					}
					
				}
			}
		}
		
	}
	
	/**
	 * Must be called from main thread to upload a vertices buffer computer in parallel
	 * to GL buffers.
	 * @param renderBuffers The render buffers written in parallel.
	 */
	public void uploadRedrawBuffers(ChunkRenderBuffers renderBuffers) {
		
		WorldRenderSequentialBuffer buff;
		for (int i = 0, bit, indices; i < LAYERS_COUNT; ++i) {
			bit = 1 << i;
			if ((this.changed & bit) == bit) {
				
				buff = renderBuffers.getBuffer(i);
				indices = buff.indices();
				buff.flip();
				
				this.drawBuffers[i].bindVao();
				this.drawBuffers[i].uploadVboData(WorldSequentialFormat.SEQUENTIAL_MAIN, buff.getData(), BufferUsage.DYNAMIC_DRAW);
				this.drawBuffers[i].uploadIboData(buff.getIndices(), BufferUsage.DYNAMIC_DRAW);
				this.drawBuffers[i].setIndicesCount(indices);
				
			}
		}
		
		if (this.changed == CHANGED_ALL) {
			this.firstUpdated = true;
		}
		
		this.doneRedrawing();
		
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
	
	private static void computeBlockFaces(BlockFaces faces, BlockState state, WorldChunk chunk, int x, int y, int z) {
		for (Direction direction : Direction.values()) {
			faces.setFaceBlock(state, direction, chunk.getBlockAtBlockRel(x, y, z, direction));
		}
	}
	
	// Utilities //
	
	/*
	 * Utility method to trigger a callback for each direction for neighbors who need updates.
	 * @param pos The block update position.
	 * @param consumer The callback for directions.
	 */
	/*private static void checkBlockOnFaces(BlockPositioned pos, Consumer<Direction> consumer) {
		
		int rx = pos.getX() & 15;
		int ry = pos.getY() & 15;
		int rz = pos.getZ() & 15;
		
		if (rx == 0) consumer.accept(Direction.SOUTH);
		else if (rx == 15) consumer.accept(Direction.NORTH);
		
		if (ry == 0) consumer.accept(Direction.BOTTOM);
		else if (ry == 15) consumer.accept(Direction.TOP);
		
		if (rz == 0) consumer.accept(Direction.WEST);
		else if (rz == 15) consumer.accept(Direction.EAST);
		
	}*/
	
}
