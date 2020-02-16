package fr.theorozier.procgen.common.world.gen.provider;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.chunk.*;

public abstract class ChunkGeneratorProvider {
	
	private final String identifier;
	
	public ChunkGeneratorProvider(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Provide a chunk generator for a given world.
	 * @param world The world dimension.
	 * @return A new chunk generator, should not return Null.
	 */
	public abstract ChunkGenerator create(WorldDimension world);
	
}
