package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.World;

public interface ChunkGeneratorProvider {

	ChunkGenerator create(World world);
	
}
