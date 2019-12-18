package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.state.BlockState;

public interface Chunk {
	
	BlockState getBlockAt(int x, int y, int z);

}
