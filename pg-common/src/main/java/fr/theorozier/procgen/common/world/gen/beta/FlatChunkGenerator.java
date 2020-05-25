package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.biome.Biomes;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.gen.biome.UniqueBiomeProvider;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

public class FlatChunkGenerator extends ChunkGenerator {
	
	public FlatChunkGenerator(long seed) {
		super(seed, new UniqueBiomeProvider(seed, Biomes.PLAIN));
	}
	
	@Override
	public void genBase(WorldAccessorServer world, WorldServerChunk chunk, BlockPositioned pos) {
		if (chunk.getChunkPos().getY() == 0 && chunk.getChunkPos().getX() == 0 && chunk.getChunkPos().getZ() == 0) {
			for (int x = 0; x < 16; ++x) {
				for (int z = 0; z < 16; ++z) {
					for (int y = 0; y < 5; ++y) {
						chunk.setBlockAt(x, y, z, Blocks.STONE.getDefaultState());
					}
				}
			}
		}
	}
	
}
