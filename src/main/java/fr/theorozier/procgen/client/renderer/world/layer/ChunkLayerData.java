package fr.theorozier.procgen.client.renderer.world.layer;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderer;
import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.world.block.BlockRenderer;
import fr.theorozier.procgen.client.renderer.world.block.BlockRenderers;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;

public abstract class ChunkLayerData {
	
	protected final Chunk chunk;
	protected final World world;
	protected final BlockRenderLayer layer;
	protected final ChunkRenderManager renderManager;
	
	protected final WorldRenderDataArray dataArray;
	
	private boolean needUpdate = false;
	
	public ChunkLayerData(Chunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager) {
		
		this.chunk = chunk;
		this.world = chunk.getWorld();
		this.layer = layer;
		this.renderManager = renderManager;
		
		this.dataArray = new WorldRenderDataArray();
		
	}
	
	public BlockRenderLayer getLayer() {
		return this.layer;
	}
	
	public boolean doNeedUpdate() {
		return this.needUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
	
	public abstract void handleNewViewPosition(ChunkRenderer cr, int x, int y, int z);
	public abstract void handleChunkUpdate(ChunkRenderer cr);
	
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
		WorldBlock worldBlock;
		BlockRenderer renderer;
		
		int cx = this.chunk.getChunkPosition().getX();
		int cy = this.chunk.getChunkPosition().getY();
		int cz = this.chunk.getChunkPosition().getZ();
		
		int wx, wy, wz;
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					
					if (!this.chunk.hasBlockAtRelative(x, y, z))
						continue;
					
					worldBlock = this.chunk.getBlockAtRelative(x, y, z);
					
					if (!worldBlock.isInRenderLayer(this.layer))
						continue;
					
					renderer = BlockRenderers.getRenderer(worldBlock.getBlockType());
					
					if (renderer != null) {
						
						wx = cx + x;
						wy = cy + y;
						wz = cz + z;
						
						if (y < 15)
							faces.setFaceBlock(worldBlock, Direction.TOP, this.chunk.getBlockAtRelative(x, y + 1, z));
						else faces.setFaceBlock(worldBlock, Direction.TOP, this.world.getBlockAt(wx, wy + 1, wz));
						
						if (y > 0)
							faces.setFaceBlock(worldBlock, Direction.BOTTOM, this.chunk.getBlockAtRelative(x, y - 1, z));
						else faces.setFaceBlock(worldBlock, Direction.BOTTOM, this.world.getBlockAt(wx, wy - 1, wz));
						
						if (x < 15)
							faces.setFaceBlock(worldBlock, Direction.NORTH, this.chunk.getBlockAtRelative(x + 1, y, z));
						else faces.setFaceBlock(worldBlock, Direction.NORTH, this.world.getBlockAt(wx + 1, wy, wz));
						
						if (x > 0)
							faces.setFaceBlock(worldBlock, Direction.SOUTH, this.chunk.getBlockAtRelative(x - 1, y, z));
						else faces.setFaceBlock(worldBlock, Direction.SOUTH, this.world.getBlockAt(wx - 1, wy, wz));
						
						if (z < 15)
							faces.setFaceBlock(worldBlock, Direction.EAST, this.chunk.getBlockAtRelative(x, y, z + 1));
						else faces.setFaceBlock(worldBlock, Direction.EAST, this.world.getBlockAt(wx, wy, wz + 1));
						
						if (z > 0)
							faces.setFaceBlock(worldBlock, Direction.WEST, this.chunk.getBlockAtRelative(x, y, z - 1));
						else faces.setFaceBlock(worldBlock, Direction.WEST, this.world.getBlockAt(wx, wy, wz - 1));
						
						consumer.accept(wx, wy, wz, worldBlock, renderer, faces);
						
					}
					
				}
			}
		}
		
	}
	
	protected interface BlockConsumer {
		void accept(int x, int y, int z, WorldBlock block, BlockRenderer renderer, BlockFaces faces);
	}
	
}
