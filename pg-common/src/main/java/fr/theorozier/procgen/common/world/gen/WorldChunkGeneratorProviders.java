package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.beta.Beta3DChunkGenerator;
import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.provider.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.provider.FastChunkGeneratorProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class WorldChunkGeneratorProviders {
	
	private static final Map<String, ChunkGeneratorProvider> providers = new HashMap<>();
	
	// DEFAULT PROVIDERS //
	public static final ChunkGeneratorProvider BETA_CHUNK_PROVIDER = registerFast("beta", world -> new BetaChunkGenerator(world.getSeed()));
	public static final ChunkGeneratorProvider BETA_3D_CHUNK_PROVIDER = registerFast("beta_3d", world -> new Beta3DChunkGenerator(world.getSeed()));
	
	// REGISTERS METHODS //
	public static <A extends ChunkGeneratorProvider> A register(A provider) {
		
		providers.put(provider.getIdentifier(), provider);
		return provider;
		
	}
	
	public static FastChunkGeneratorProvider registerFast(String identifier, Function<WorldDimension, ChunkGenerator> provider) {
		return register(new FastChunkGeneratorProvider(identifier, provider));
	}
	
	public static ChunkGeneratorProvider get(String identifier) {
		return providers.get(identifier);
	}
	
	private WorldChunkGeneratorProviders() {}

}
