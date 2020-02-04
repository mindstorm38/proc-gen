package fr.theorozier.procgen.client.renderer.world.layer;

import fr.theorozier.procgen.client.renderer.block.BlockColorResolver;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderer;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.common.util.Color;

import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

public class ChunkDirectLayerData extends ChunkLayerData {
	
	public ChunkDirectLayerData(WorldChunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager) {
		super(chunk, layer, renderManager);
	}
	
	@Override
	public void handleNewViewPosition(ChunkRenderer cr, int x, int y, int z) {}
	
	@Override
	public void handleChunkUpdate(ChunkRenderer cr) {
		
		this.refreshRenderOffsets();
		this.renderManager.scheduleUpdateTask(cr, this.layer, this::rebuildData);
		
	}
	
	private void rebuildData() {
		this.rebuildData(this.renderManager.getWorldRenderer().getTerrainMap());
	}
	
	public void rebuildData(TextureMap terrainMap) {
		
		this.rebuildArrays(() -> {
			this.foreachBlocks((bx, by, bz, block, renderer, faces) -> {
				
				try {
					renderer.getRenderData(this.world, block, bx, by, bz, bx + this.roX, by, bz + this.roZ, faces, terrainMap, this.dataArray);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Failed to render a block at " + bx + "/" + by + "/" + bz, e);
				}
				
			});
		});
		
	}
	
}
