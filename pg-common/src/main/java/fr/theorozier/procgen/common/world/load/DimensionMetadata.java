package fr.theorozier.procgen.common.world.load;

import java.util.*;

public class DimensionMetadata {

	private final long seed;
	private final String chunkGeneratorProvider;
	
	public DimensionMetadata(long seed, String chunkGeneratorProvider) {
		
		this.seed = seed;
		this.chunkGeneratorProvider = Objects.requireNonNull(chunkGeneratorProvider);
		
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public String getChunkGeneratorProvider() {
		return this.chunkGeneratorProvider;
	}

}
