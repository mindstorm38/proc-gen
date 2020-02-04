package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.gen.beta.Beta3DChunkGenerator;
import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;

import java.util.HashMap;
import java.util.Map;

public final class WorldGenerators {
	
	private static final Map<String, ChunkGeneratorProvider> chunkGeneratorProviders = new HashMap<>();
	
	// DEFAULT PROVIDERS //
	public static final ChunkGeneratorProvider BETA_CHUNK_PROVIDER = registerChunkGeneratorProvider("beta", world -> new BetaChunkGenerator(world.getSeed()));
	public static final ChunkGeneratorProvider BETA_3D_CHUNK_PROVIDER = registerChunkGeneratorProvider("beta_3d", world -> new Beta3DChunkGenerator(world.getSeed()));
	
	// REGISTERS METHODS //
	public static <A extends ChunkGeneratorProvider> A registerChunkGeneratorProvider(String identifier, A provider) {
		
		chunkGeneratorProviders.put(identifier, provider);
		return provider;
		
	}
	
	public static ChunkGeneratorProvider getChunkGeneratorProvider(String identifier) {
		return chunkGeneratorProviders.get(identifier);
	}
	
	private WorldGenerators() {}

}
