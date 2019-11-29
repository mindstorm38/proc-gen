package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;

import java.util.Random;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;

public abstract class ChunkGenerator {
	
	private static final int HALF_CHUNK_POSITION = CHUNK_SIZE / 2 - 1;
	
	protected final long seed;
	protected final BiomeProvider biomeProvider;
	
	public ChunkGenerator(long seed, BiomeProvider biomeProvider) {
		
		this.seed = seed;
		this.biomeProvider = biomeProvider;
		
	}
	
	public void genBiomes(Chunk chunk, BlockPosition pos) {
	
		Biome[] biomes = this.biomeProvider.getBiomes(pos.getX(), pos.getZ(), CHUNK_SIZE, CHUNK_SIZE);
		chunk.setBiomes(biomes);
		
	}
	
	public abstract void genBase(Chunk chunk, BlockPosition pos);
	public abstract void genSurface(Chunk chunk, BlockPosition pos);
	
	public void genFeatures(Chunk chunk, BlockPosition pos) {
	
		Biome biome = chunk.getBiomeAtRelative(HALF_CHUNK_POSITION, HALF_CHUNK_POSITION);
		Random random = new Random();
		
		for (ConfiguredFeature<?> feature : biome.getConfiguredFeatures()) {
			
			random.setSeed(getFeatureSeed(this.seed, pos.getX(), pos.getZ()));
			feature.place(chunk.getWorld(), this, random, pos);
			
		}
		
	}
	
	public static long getFeatureSeed(long seed, int x, int y) {
		return seed * x * 80536756274218L + y * 50572864577051L;
	}
	
}
