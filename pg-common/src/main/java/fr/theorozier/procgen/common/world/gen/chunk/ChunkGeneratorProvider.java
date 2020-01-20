package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.world.WorldServer;

public interface ChunkGeneratorProvider {

	ChunkGenerator create(WorldServer world);
	
}
