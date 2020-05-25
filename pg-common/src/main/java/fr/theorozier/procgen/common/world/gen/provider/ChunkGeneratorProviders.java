package fr.theorozier.procgen.common.world.gen.provider;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.beta.Beta3DChunkGenerator;
import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;
import fr.theorozier.procgen.common.world.gen.beta.FlatChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ChunkGeneratorProviders {
	
	private static final Map<String, ChunkGeneratorProvider> providers = new HashMap<>();
	
	// DEFAULT PROVIDERS //
	public static final ChunkGeneratorProvider BETA_CHUNK_PROVIDER = registerDirect("beta", world -> new BetaChunkGenerator(world.getSeed()));
	public static final ChunkGeneratorProvider BETA_3D_CHUNK_PROVIDER = registerDirect("beta_3d", world -> new Beta3DChunkGenerator(world.getSeed()));
	public static final ChunkGeneratorProvider FLAT_CHUNK_PROVIDER = registerDirect("flat", world -> new FlatChunkGenerator(world.getSeed()));
	
	// REGISTERS METHODS //
	public static <A extends ChunkGeneratorProvider> A register(A provider) {
		
		providers.put(provider.getIdentifier(), provider);
		return provider;
		
	}
	
	public static DirectChunkGeneratorProvider registerDirect(String identifier, Function<WorldDimension, ChunkGenerator> provider) {
		return register(new DirectChunkGeneratorProvider(identifier, provider));
	}
	
	public static ChunkGeneratorProvider get(String identifier) {
		return providers.get(identifier);
	}
	
	private ChunkGeneratorProviders() {}

}
