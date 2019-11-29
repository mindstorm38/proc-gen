package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.chunk.Section;
import fr.theorozier.procgen.world.chunk.SectionPosition;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;

import java.util.Random;

public abstract class ChunkGenerator {
	
	protected final long seed;
	protected final BiomeProvider biomeProvider;
	
	public ChunkGenerator(long seed, BiomeProvider biomeProvider) {
		
		this.seed = seed;
		this.biomeProvider = biomeProvider;
		
	}
	
	public void genBiomes(Section section, SectionPosition pos) {
		
		Biome[] biomes = this.biomeProvider.getBiomes(pos.getX(), pos.getZ(), 16, 16);
		section.setBiomes(biomes);
		
	}
	
	public abstract void genBase(Chunk chunk, BlockPosition pos);
	
	public abstract void genSurface(Section section, SectionPosition pos);
	
	public void genFeatures(Section section, SectionPosition pos) {
	
		Biome biome = section.getBiomeAtRelative(7, 7);
		Random random = new Random();
		
		for (ConfiguredFeature<?> feature : biome.getConfiguredFeatures()) {
			
			random.setSeed(getFeatureSeed(this.seed, pos.getX(), pos.getZ()));
			feature.place(section.getWorld(), this, random, pos.getChunkPos(0));
			
		}
		
	}
	
	public static long getFeatureSeed(long seed, int x, int y) {
		return seed * x * 80536756274218L + y * 50572864577051L;
	}
	
}
