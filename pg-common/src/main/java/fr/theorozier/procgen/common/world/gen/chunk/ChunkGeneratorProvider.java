package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.world.WorldDimension;

@FunctionalInterface
public interface ChunkGeneratorProvider {
	
	/**
	 * Provide a chunk generator for a given world.
	 * @param world The world dimension.
	 * @return A new chunk generator, should not return Null.
	 */
	ChunkGenerator create(WorldDimension world);
	
}
