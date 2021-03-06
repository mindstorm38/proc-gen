package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.biome.surface.BiomeSurface;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.common.world.gen.biome.BiomeProvider;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.msengine.common.util.noise.OctaveSimplexNoise;

import java.util.EnumSet;
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
	
	public long getSeed() {
		return this.seed;
	}
	
	public void genBiomes(WorldAccessorServer world, WorldServerSection section, SectionPositioned pos) {
		
		Biome[] biomes = this.biomeProvider.getBiomes(pos.getX() << 4, pos.getZ() << 4, 16, 16);
		section.setBiomes(biomes);
		
	}
	
	public void genSectionBase(WorldAccessorServer world, WorldServerSection section, SectionPositioned pos) {
		
		section.forEachChunk(chunk -> {
			this.genBase(world, (WorldServerChunk) chunk, chunk.getChunkPos());
		});
		
		section.recomputeHeightmap(EnumSet.of(Heightmap.Type.WORLD_BASE_SURFACE, Heightmap.Type.WORLD_BASE_WATER_SURFACE));
		
	}
	
	public abstract void genBase(WorldAccessorServer world, WorldServerChunk chunk, BlockPositioned pos);
	
	public void genBedrock(WorldAccessorServer world, WorldServerSection section, SectionPositioned pos) { }
	
	public void genSurface(WorldAccessorServer world, WorldServerSection section, SectionPositioned pos) {
		
		this.genBedrock(world, section, pos);
		
		int seaLimit = section.getWorld().getSeaLevel() - 2;
		
		short height, baseHeight;
		Biome biome;
		BiomeSurface surface;
		BlockState newBlock, block = null;
		
		int sx = pos.getX() << 4;
		int sz = pos.getZ() << 4;
		
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
	
	public void genFeatures(WorldAccessorServer world, WorldServerSection section, SectionPositioned pos) {
		
		Biome biome = section.getBiomeAt(7, 7);
		Random random = new Random();
		
		long featureSeed = getFeatureSeed(this.seed, pos.getX(), pos.getZ());
		int featureIdx = 0;
		
		ImmutableBlockPosition at = new ImmutableBlockPosition(pos.getX() << 4, 0, pos.getZ() << 4);
		
		for (ConfiguredFeature<?> feature : biome.getConfiguredFeatures()) {
			
			random.setSeed(featureSeed * (++featureIdx));
			feature.place(world, this, random, at);
			
		}
		
	}
	
	public static long getFeatureSeed(long seed, int x, int y) {
		return seed * x * 80536756274218L + y * 50572864577051L;
	}
	
}
