package fr.theorozier.procgen.common.world.load;

import com.google.gson.JsonObject;

import java.util.Objects;

public class DimensionMetadata {

	private final long seed;
	private final String chunkGeneratorProvider;
	
	private final JsonObject data;
	
	public DimensionMetadata(long seed, String chunkGeneratorProvider, JsonObject data) {
		
		this.seed = seed;
		this.chunkGeneratorProvider = Objects.requireNonNull(chunkGeneratorProvider);
		this.data = data;
		
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public String getChunkGeneratorProvider() {
		return this.chunkGeneratorProvider;
	}
	
	public JsonObject getData() {
		return this.data;
	}
	
}
