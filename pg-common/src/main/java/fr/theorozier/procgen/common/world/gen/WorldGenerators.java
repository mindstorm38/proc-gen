package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;

import java.util.HashMap;
import java.util.Map;

public final class WorldGenerators {
	
	private static final Map<String, ChunkGeneratorProvider> chunkGeneratorProviders = new HashMap<>();
	//private static final Map<String, WorldDimensionHandler> worldDimensionHandlers = new HashMap<>();
	
	// DEFAULT PROVIDERS //
	public static final ChunkGeneratorProvider BETA_CHUNK_PROVIDER = registerChunkGeneratorProvider("beta", world -> new BetaChunkGenerator(world.getSeed()));
	
	// DEFAULT WORLD DIM HANDLER //
	//public static final BetaWorldDimensionHandler BETA_WORLD_DIMENSION_HANDLER = registerWorldDimensionHandler("beta", new BetaWorldDimensionHandler());
	
	// REGISTERS METHODS //
	public static <A extends ChunkGeneratorProvider> A registerChunkGeneratorProvider(String identifier, A provider) {
		
		chunkGeneratorProviders.put(identifier, provider);
		return provider;
		
	}
	
	public static ChunkGeneratorProvider getChunkGeneratorProvider(String identifier) {
		return chunkGeneratorProviders.get(identifier);
	}
	
	/*public static <A extends WorldDimensionHandler> A registerWorldDimensionHandler(String identifier, A handler) {
		
		worldDimensionHandlers.put(identifier, handler);
		return handler;
		
	}*/
	
	/*public static WorldDimensionHandler getWorldDimensionHandler(String identifier) {
		return worldDimensionHandlers.get(identifier);
	}*/
	
	private WorldGenerators() {}

}
