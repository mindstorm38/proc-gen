package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.chunk.*;
import fr.theorozier.procgen.common.world.biome.surface.BiomeSurface;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
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
	
	public void genBiomes(WorldServerSection section, SectionPositioned pos) {
		
		Biome[] biomes = this.biomeProvider.getBiomes(pos.getX(), pos.getZ(), 16, 16);
		section.setBiomes(biomes);
		
	}
	
	public abstract void genBase(WorldServerChunk chunk, BlockPositioned pos);
	
	public void genSurface(WorldServerSection section, SectionPositioned pos) {
		
		WorldServer world = section.getWorldServer();
		int seaLimit = world.getSeaLevel() - 2;
		
		short height, baseHeight;
		Biome biome;
		BiomeSurface surface;
		BlockState newBlock, block = null;
		
		int sx = pos.getX();
		int sz = pos.getZ();
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				
				height = (short) (section.getHeightAt(Heightmap.Type.WORLD_BASE_SURFACE, x, z) - 1);
				
				biome = section.getBiomeAt(x, z);
				surface = height > seaLimit ? biome.getSurface() : biome.getUnderwaterSurface();
				
				baseHeight = (short) (surface.getBaseHeight() + this.surfaceNoise.noise(sx + x, sz + z, 0.1f));
				
				for (short y = 0; y < baseHeight; ++y) {
					
					newBlock = surface.getLayer(y);
					
					if (newBlock != null)
						block = newBlock;
					
					section.setBlockAt(x, height - y, z, block);
					
				}
				
			}
		}
		
	}
	
	public void genFeatures(WorldServerSection section, SectionPositioned pos) {
	
		Biome biome = section.getBiomeAt(7, 7);
		Random random = new Random();
		
		long featureSeed = getFeatureSeed(this.seed, pos.getX(), pos.getZ());
		int featureIdx = 0;
		
		ImmutableBlockPosition at = new ImmutableBlockPosition(pos, 0);
		
		for (ConfiguredFeature<?> feature : biome.getConfiguredFeatures()) {
			
			random.setSeed(featureSeed * (++featureIdx));
			feature.place(section.getWorldServer(), this, random, at);
			
		}
		
	}
	
	public static long getFeatureSeed(long seed, int x, int y) {
		return seed * x * 80536756274218L + y * 50572864577051L;
	}
	
}
