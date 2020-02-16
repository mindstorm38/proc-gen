package fr.theorozier.procgen.common.world.gen.provider;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;

import java.util.Objects;
import java.util.function.Function;

public class FastChunkGeneratorProvider extends ChunkGeneratorProvider {
	
	private final Function<WorldDimension, ChunkGenerator> provider;
	
	public FastChunkGeneratorProvider(String identifier, Function<WorldDimension, ChunkGenerator> provider) {
		super(identifier);
		this.provider = Objects.requireNonNull(provider);
	}
	
	@Override
	public ChunkGenerator create(WorldDimension world) {
		return this.provider.apply(world);
	}
	
}
