package fr.theorozier.procgen.client.renderer.world.layer;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.client.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.world.chunk.Chunk;

public interface ChunkLayerDataProvider {
	
	ChunkLayerData provide(Chunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager);
	
}
