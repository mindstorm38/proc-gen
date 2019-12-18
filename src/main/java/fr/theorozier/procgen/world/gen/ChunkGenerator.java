package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.biome.surface.BiomeSurface;
import fr.theorozier.procgen.world.chunk.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import io.msengine.common.util.noise.OctaveSimplexNoise;

import java.util.Random;

public abstract class ChunkGenerator {
	
	protected final long seed;
	protected final BiomeProvider biomeProvider;
	
	private final OctaveSimplexNoise surfaceNoise;
	
	public ChunkGenerator(long seed, BiomeProvider biomeProvider) {
		
		this.seed = seed;
		this.biomeProvider = biomeProvider;
		
		this.surfaceNoise = new OctaveSimplexNoise(seed, 4, 0.4f, 2.0f);
		
	}
	
	public void genBiomes(Section section, SectionPosition pos) {
		
		Biome[] biomes = this.biomeProvider.getBiomes(pos.getX(), pos.getZ(), 16, 16);
		section.setBiomes(biomes);
		
	}
	
	public abstract void genBase(Chunk chunk, BlockPosition pos);
	
	public void genSurface(Section section, SectionPosition pos) {
		
		World world = section.getWorld();
		int seaLimit = world.getSeaLevel() - 2;
		
		short height, baseHeight;
		Biome biome;
		BiomeSurface surface;
		Block newBlock, block = null;
		WorldBlock worldBlock;
		
		int sx = pos.getX();
		int sz = pos.getZ();
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				
				height = (short) (section.getHeightAt(Heightmap.Type.WORLD_BASE_SURFACE, x, z) - 1);
				
				biome = section.getBiomeAtRelative(x, z);
				surface = height > seaLimit ? biome.getSurface() : biome.getUnderwaterSurface();
				
				baseHeight = (short) (surface.getBaseHeight() + this.surfaceNoise.noise(sx + x, sz + z, 0.1f));
				
				for (short y = 0; y < baseHeight; ++y) {
					
					newBlock = surface.getLayer(y);
					
					if (newBlock != null)
						block = newBlock;
					
					worldBlock = section.getBlockAtRelative(x, height - y, z);
					worldBlock.setBlockType(block);
					
				}
				
			}
		}
		
	}
	
	public void genFeatures(Section section, SectionPosition pos) {
	
		Biome biome = section.getBiomeAtRelative(7, 7);
		Random random = new Random();
		
		long featureSeed = getFeatureSeed(this.seed, pos.getX(), pos.getZ());
		int featureIdx = 0;
		
		for (ConfiguredFeature<?> feature : biome.getConfiguredFeatures()) {
			
			random.setSeed(featureSeed * (++featureIdx));
			feature.place(section.getWorld(), this, random, pos.getChunkPos(0));
			
		}
		
	}
	
	public static long getFeatureSeed(long seed, int x, int y) {
		return seed * x * 80536756274218L + y * 50572864577051L;
	}
	
}
