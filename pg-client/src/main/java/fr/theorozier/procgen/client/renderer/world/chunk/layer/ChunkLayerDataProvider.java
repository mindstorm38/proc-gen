package fr.theorozier.procgen.client.renderer.world.chunk.layer;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;

public interface ChunkLayerDataProvider {
	
	ChunkLayerData provide(BlockRenderLayer layer, ChunkRenderManager renderManager);
	
}
