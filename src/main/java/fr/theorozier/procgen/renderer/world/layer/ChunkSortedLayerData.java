package fr.theorozier.procgen.renderer.world.layer;

import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.renderer.world.ChunkRenderer;
import fr.theorozier.procgen.renderer.world.block.BlockFaces;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;

import java.util.ArrayList;
import java.util.List;

public class ChunkSortedLayerData extends ChunkLayerData {
	
	private final List<ChunkCompiledBlock> cache;
	
	public ChunkSortedLayerData(Chunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager) {
		
		super(chunk, layer, renderManager);
		
		this.cache = new ArrayList<>();
		
	}
	
	@Override
	public void handleNewViewPosition(ChunkRenderer cr, int x, int y, int z) {
		
		//this.sortCache(x, y, z);
		//this.rebuildData(renderer.getTerrainMap());
		
	}
	
	@Override
	public void handleChunkUpdate(ChunkRenderer cr) {
		this.renderManager.scheduleUpdateTask(cr, this.layer, this::rebuildCacheAndData);
	}
	
	private void rebuildCacheAndData() {
		
		this.rebuildCache();
		this.rebuildData(this.renderManager.getWorldRenderer().getTerrainMap());
		
	}
	
	public void rebuildCache() {
		
		this.cache.clear();
		
		this.foreachBlocks((x, y, z, block, renderer, faces) -> {
			this.cache.add(new ChunkCompiledBlock(renderer, block, faces));
		});
		
	}
	
	public void sortCache(float viewX, float viewY, float viewZ) {
		
		this.cache.forEach(cp -> cp.recomputeDistanceTo(viewX, viewY, viewZ));
		this.cache.sort(ChunkCompiledBlock::compareTo);
		
	}
	
	public void rebuildData(TextureMap terrainMap) {
		
		this.rebuildArrays(() -> {
			
			BlockFaces faces = new BlockFaces();
			WorldBlock block;
			
			for (ChunkCompiledBlock compiledBlock : this.cache) {
				
				block = compiledBlock.getBlock();
				compiledBlock.mutateBlockFaces(faces);
				
				compiledBlock.getRenderer().getRenderData(block, block.getX(), block.getY(), block.getZ(), faces, terrainMap, this.dataArray);
				
			}
			
		});
		
	}
	
}
