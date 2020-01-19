package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;

import java.util.HashMap;
import java.util.Map;

public final class WorldGenerators {
	
	private static final Map<String, ChunkGeneratorProvider> chunkGeneratorProviders = new HashMap<>();
	private static final Map<String, DimensionHandler> dimensionHandlers = new HashMap<>();
	
	public static final ChunkGeneratorProvider BETA_CHUNK_PROVIDER = registerChunkGeneratorProvider("beta", world -> new BetaChunkGenerator(world.getSeed()));
	
	public static <A extends ChunkGeneratorProvider> A registerChunkGeneratorProvider(String identifier, A provider) {
		
		chunkGeneratorProviders.put(identifier, provider);
		return provider;
		
	}
	
	public static ChunkGeneratorProvider getChunkGeneratorProvider(String identifier) {
		return chunkGeneratorProviders.get(identifier);
	}
	
	public static <A extends DimensionHandler> A registerDimensionHandler(String identifier, A handler) {
		
		dimensionHandlers.put(identifier, handler);
		return handler;
		
	}
	
	public static DimensionHandler getDimensionHandler(String identifier) {
		return dimensionHandlers.get(identifier);
	}
	
	private WorldGenerators() {}

}
