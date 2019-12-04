package fr.theorozier.procgen.renderer.world.layer;

import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.WorldRenderer;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.renderer.texture.TextureMap;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkDirectLayerData extends ChunkLayerData {
	
	public ChunkDirectLayerData(Chunk chunk, BlockRenderLayer layer) {
		super(chunk, layer);
	}
	
	@Override
	public void handleNewViewPosition(WorldRenderer renderer, int x, int y, int z) {}
	
	@Override
	public void handleChunkUpdate(WorldRenderer renderer) {
		this.rebuildData(renderer.getTerrainMap());
	}
	
	public void rebuildData(TextureMap terrainMap) {
		
		this.rebuildArrays(() -> {
			
			final AtomicInteger lidx = new AtomicInteger(0);
			
			this.foreachBlocks((x, y, z, block, renderer, faces) -> {
				lidx.set(renderer.getRenderData(block, x, y, z, lidx.get(), faces, terrainMap, this.vertices, this.texcoords, this.indices));
			});
			
		});
		
	}
	
}
