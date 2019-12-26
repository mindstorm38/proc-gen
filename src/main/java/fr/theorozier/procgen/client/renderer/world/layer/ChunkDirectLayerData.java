package fr.theorozier.procgen.client.renderer.world.layer;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderer;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import io.msengine.client.renderer.texture.TextureMap;
import io.sutil.ThreadUtils;

public class ChunkDirectLayerData extends ChunkLayerData {
	
	public ChunkDirectLayerData(WorldChunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager) {
		super(chunk, layer, renderManager);
	}
	
	@Override
	public void handleNewViewPosition(ChunkRenderer cr, int x, int y, int z) {}
	
	@Override
	public void handleChunkUpdate(ChunkRenderer cr) {
		this.renderManager.scheduleUpdateTask(cr, this.layer, this::rebuildData);
	}
	
	private void rebuildData() {
		this.rebuildData(this.renderManager.getWorldRenderer().getTerrainMap());
	}
	
	public void rebuildData(TextureMap terrainMap) {
		
		this.rebuildArrays(() -> {
			this.foreachBlocks((x, y, z, block, renderer, faces) -> {
				renderer.getRenderData(this.world, block, x, y, z, faces, terrainMap, this.dataArray);
			});
		});
		
	}
	
}
