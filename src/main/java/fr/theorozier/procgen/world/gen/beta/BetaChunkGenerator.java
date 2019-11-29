package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.TreeFeature;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;
import io.msengine.common.util.noise.OctaveSimplexNoise;
import io.msengine.common.util.noise.SeedSimplexNoise;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;
import static fr.theorozier.procgen.world.World.MAX_WORLD_HEIGHT;

public class BetaChunkGenerator extends ChunkGenerator {
	
	public static final ChunkGeneratorProvider PROVIDER = world -> new BetaChunkGenerator(world.getSeed());
	
	private final OctaveSimplexNoise surfaceNoise;
	
	private final ConfiguredFeature<FeatureConfig> testFeature = new ConfiguredFeature<>(new TreeFeature(), FeatureConfig.EMPTY);
	
	public BetaChunkGenerator(long seed) {
		
		super(seed, new BetaBiomeProvider(seed));
		
		this.surfaceNoise = new OctaveSimplexNoise(seed, 16, 0.4f, 2.0f);
		
	}
	
	private Biome getBiomeAtRelative(Chunk chunk, BlockPosition pos, int dx, int dz) {
		
		if (dx < 0 || dx >= CHUNK_SIZE || dz < 0 || dz >= CHUNK_SIZE) {
			return this.biomeProvider.getBiomeAt(pos.getX() + dx, pos.getZ() + dz);
		} else {
			return chunk.getBiomeAtRelative(dx, dz);
		}
		
	}
	
	private static final byte TRANSITION_DIST = 1;
	private static final byte FULL_DIST = TRANSITION_DIST * 2 + 1;
	private static final float DIST_RATIO = (float) TRANSITION_DIST / (float) FULL_DIST;
	
	private static final byte[] X_OFFSETS = {1, 1, 0, -1};
	private static final byte[] Z_OFFSETS = {0, 1, 1,  1};
	private static final byte OFFSETS_COUNT = 4;
	
	@Override
	public void genBase(Chunk chunk, BlockPosition pos) {
		
		Biome biome, nbiome1, nbiome2;
		float depth, scale, noise;
		byte neighbourBiomes;
		int wx, wy, wz;
		int nx, nz;
		WorldBlock block;
		
		for (int x = 0; x < CHUNK_SIZE; ++x) {
			for (int z = 0; z < CHUNK_SIZE; ++z) {
				
				wx = pos.getX() + x;
				wz = pos.getZ() + z;
				
				biome = chunk.getBiomeAtRelative(x, z);
				depth = biome.getDepth();
				scale = biome.getScale();
				neighbourBiomes = 1;
				
				for (int i = 0; i < OFFSETS_COUNT; i++) {
					
					nbiome1 = this.getBiomeAtRelative(chunk, pos, x + X_OFFSETS[i], z - Z_OFFSETS[i]);
					nbiome2 = this.getBiomeAtRelative(chunk, pos, x - X_OFFSETS[i], z + Z_OFFSETS[i]);
					
					if (nbiome1 != nbiome2) {
						
						depth += MathUtils.lerp(nbiome1.getDepth(), nbiome2.getDepth(), DIST_RATIO);
						scale += MathUtils.lerp(nbiome1.getScale(), nbiome2.getScale(), DIST_RATIO);
						neighbourBiomes++;
						
					}
					
				}
				
				depth /= neighbourBiomes;
				scale /= neighbourBiomes;
				noise = (depth * MAX_WORLD_HEIGHT) + (this.surfaceNoise.noise(wx, wz, 0.004f) + 1) * scale;
				
				for (int y = 0; y < CHUNK_SIZE; ++y) {
					
					wy = pos.getY() + y;
					
					block = chunk.getBlockAtRelative(x, y, z);
					
					if (wy == 0) {
						block.setBlockType(Blocks.BEDROCK);
					} else if (wy < noise - 4) {
						block.setBlockType(Blocks.STONE);
					} else if (wy < noise - 1) {
						block.setBlockType(Blocks.DIRT);
					} else if (wy < noise) {
						
						block.setBlockType(Blocks.GRASS);
						chunk.setHeightAtRelative(x, z, (byte) (y + 1));
						
					} else {
						block.setBlockType(Blocks.AIR);
					}
					
				}
				
			}
		}
		
	}
	
	@Override
	public void genSurface(Chunk chunk, BlockPosition pos) {
		// TODO: Generate surface (sand, grass, stone) from biome preferences.
	}
	
	private static long getDecorationSeed(long seed) {
		return seed * 993402349510639L;
	}
	
}
