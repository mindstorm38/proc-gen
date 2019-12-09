package fr.theorozier.procgen.renderer.world.layer;

import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.renderer.world.ChunkRenderManager;
import fr.theorozier.procgen.world.chunk.Chunk;

public interface ChunkLayerDataProvider {
	
	ChunkLayerData provide(Chunk chunk, BlockRenderLayer layer, ChunkRenderManager renderManager);
	
}
