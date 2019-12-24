package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldServer;

public interface ChunkGeneratorProvider {

	ChunkGenerator create(WorldServer world);
	
}
