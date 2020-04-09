package fr.theorozier.procgen.client.renderer.world.chunk.layer;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.chunk.ChunkRenderer;
import fr.theorozier.procgen.client.renderer.world.util.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.client.renderer.block.BlockRenderers;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.position.Direction;

public abstract class ChunkLayerData {
	
	protected final BlockRenderLayer layer;
	protected final ChunkRenderManager renderManager;
	protected final WorldRenderDataArray dataArray;
	
	protected WorldChunk chunk = null;
	protected WorldBase world = null;
	
	private boolean needUpdate = false;
	
	// protected int roX, roZ;
	
	public ChunkLayerData(BlockRenderLayer layer, ChunkRenderManager renderManager) {
		
		this.layer = layer;
		this.renderManager = renderManager;
		this.dataArray = new WorldRenderDataArray();
		
	}
	
	public BlockRenderLayer getLayer() {
		return this.layer;
	}
	
	public void setChunk(WorldChunk chunk/*, int roX, int roZ*/) {
		
		this.chunk = chunk;
		this.world = chunk.getWorld();
		// this.roX = roX;
		// this.roZ = roZ;
		
		this.setNeedUpdate(true);
		
	}
	
	public boolean doNeedUpdate() {
		return this.needUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
	
	public abstract void handleNewViewPosition(ChunkRenderer cr, int x, int y, int z);
	public abstract void handleChunkUpdate(ChunkRenderer cr);
	
	/*
	protected void refreshRenderOffsets() {
		
		this.roX = this.renderManager.getRenderOffsetX();
		this.roZ = this.renderManager.getRenderOffsetZ();
		
	}
	*/
	
	protected void rebuildArrays(Runnable run) {
		
		this.dataArray.resetBuffers();
		run.run();
		this.dataArray.checkOverflows();
		
	}
	
	public WorldRenderDataArray getDataArray() {
		return this.dataArray;
	}
	
	// Utils //
	
	protected void foreachBlocks(BlockConsumer consumer) {
		
		BlockFaces faces = new BlockFaces();
		BlockRenderer renderer;
		BlockState state;
		
		int cx = this.chunk.getChunkPos().getX() << 4;
		int cy = this.chunk.getChunkPos().getY() << 4;
		int cz = this.chunk.getChunkPos().getZ() << 4;
		
		int wx, wy, wz;
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					
					if ((state = this.chunk.getBlockAt(x, y, z)) == null)
						continue;
					
					if (!state.isInRenderLayer(this.layer))
						continue;
					
					renderer = BlockRenderers.getRenderer(state.getBlock());
					
					if (renderer != null) {
						
						wx = cx + x;
						wy = cy + y;
						wz = cz + z;
						
						if (renderer.needFaces()) {
							
							if (y < 15)
								faces.setFaceBlock(state, Direction.TOP, this.chunk.getBlockAt(x, y + 1, z));
							else faces.setFaceBlock(state, Direction.TOP, this.world.getBlockAt(wx, wy + 1, wz));
							
							if (y > 0)
								faces.setFaceBlock(state, Direction.BOTTOM, this.chunk.getBlockAt(x, y - 1, z));
							else faces.setFaceBlock(state, Direction.BOTTOM, this.world.getBlockAt(wx, wy - 1, wz));
							
							if (x < 15)
								faces.setFaceBlock(state, Direction.NORTH, this.chunk.getBlockAt(x + 1, y, z));
							else faces.setFaceBlock(state, Direction.NORTH, this.world.getBlockAt(wx + 1, wy, wz));
							
							if (x > 0)
								faces.setFaceBlock(state, Direction.SOUTH, this.chunk.getBlockAt(x - 1, y, z));
							else faces.setFaceBlock(state, Direction.SOUTH, this.world.getBlockAt(wx - 1, wy, wz));
							
							if (z < 15)
								faces.setFaceBlock(state, Direction.EAST, this.chunk.getBlockAt(x, y, z + 1));
							else faces.setFaceBlock(state, Direction.EAST, this.world.getBlockAt(wx, wy, wz + 1));
							
							if (z > 0)
								faces.setFaceBlock(state, Direction.WEST, this.chunk.getBlockAt(x, y, z - 1));
							else faces.setFaceBlock(state, Direction.WEST, this.world.getBlockAt(wx, wy, wz - 1));
							
							if (faces.isVisible()) {
								consumer.accept(wx, wy, wz, x, y, z, state, renderer, faces);
							}
							
						} else {
							consumer.accept(wx, wy, wz, x, y, z, state, renderer, faces);
						}
						
					}
					
				}
			}
		}
		
	}
	
	protected interface BlockConsumer {
		void accept(int wx, int wy, int wz, int bx, int by, int bz, BlockState block, BlockRenderer renderer, BlockFaces faces);
	}
	
}
