package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.Section;
import fr.theorozier.procgen.world.chunk.SectionPosition;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;
import io.msengine.common.util.noise.OctaveSimplexNoise;

public class BetaChunkGenerator extends ChunkGenerator {
	
	public static final ChunkGeneratorProvider PROVIDER = world -> new BetaChunkGenerator(world.getSeed());
	
	private static final byte TRANSITION_DIST = 1;
	private static final byte FULL_DIST = TRANSITION_DIST * 2 + 1;
	private static final float DIST_RATIO = (float) TRANSITION_DIST / (float) FULL_DIST;
	
	private static final byte[] X_OFFSETS = {1, 1, 0, -1};
	private static final byte[] Z_OFFSETS = {0, 1, 1,  1};
	private static final byte OFFSETS_COUNT = 4;
	
	// Class //
	
	private final OctaveSimplexNoise surfaceNoise;
	
	private final int noiseHorizontalGranularity;
	private final int noiseHorizontalSize;
	
	public BetaChunkGenerator(long seed) {
		
		super(seed, new BetaBiomeProvider(seed));
		
		this.surfaceNoise = new OctaveSimplexNoise(seed, 16, 0.4f, 2.0f);
		
		this.noiseHorizontalGranularity = 4;
		this.noiseHorizontalSize = 16 / this.noiseHorizontalGranularity;
		
	}
	
	private Biome getBiomeAtRelative(Chunk chunk, BlockPosition pos, int dx, int dz) {
		
		if (dx < 0 || dx >= 16 || dz < 0 || dz >= 16) {
			return this.biomeProvider.getBiomeAt(pos.getX() + dx, pos.getZ() + dz);
		} else {
			return chunk.getSection().getBiomeAtRelative(dx, dz);
		}
		
	}
	
	@Override
	public void genBase(Chunk chunk, BlockPosition pos) {
		
		float[][] randomMap = new float[this.noiseHorizontalSize + 1][this.noiseHorizontalSize + 1];
		
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;
		
		int maxHeight = chunk.getWorld().getWorldHeightLimit();
		Biome biome;
		
		for (int x = 0; x <= this.noiseHorizontalSize; ++x) {
			for (int z = 0; z <= this.noiseHorizontalSize; ++z) {
				
				if (x == this.noiseHorizontalSize || z == this.noiseHorizontalSize) {
					biome = this.biomeProvider.getBiomeAt(pos.getX() + x * this.noiseHorizontalGranularity, pos.getZ() + z * this.noiseHorizontalGranularity);
				} else {
					biome = chunk.getSection().getBiomeAtRelative(x * this.noiseHorizontalGranularity, z * this.noiseHorizontalGranularity);
				}
				
				randomMap[x][z] = (biome.getDepth() * maxHeight) + this.surfaceNoise.noise(chunkX * this.noiseHorizontalSize + x, chunkZ * this.noiseHorizontalSize + z, 0.04f) * biome.getScale();
			
			}
		}
		
		int noiseX, noiseZ, relativeX, relativeZ;
		float ratioX, ratioZ;
		float noise1, noise2, noise3, noise4;
		float noiseInt1, noiseInt2, noise;
		
		WorldBlock block;
		int wy;
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				
				noiseX = x / this.noiseHorizontalGranularity;
				noiseZ = z / this.noiseHorizontalGranularity;
				relativeX = x % this.noiseHorizontalGranularity;
				relativeZ = z % this.noiseHorizontalGranularity;
				
				ratioX = (float) relativeX / (float) this.noiseHorizontalGranularity;
				ratioZ = (float) relativeZ / (float) this.noiseHorizontalGranularity;
				
				noise1 = randomMap[noiseX    ][noiseZ    ];
				noise2 = randomMap[noiseX + 1][noiseZ    ];
				noise3 = randomMap[noiseX    ][noiseZ + 1];
				noise4 = randomMap[noiseX + 1][noiseZ + 1];
				
				noiseInt1 = MathUtils.lerp(noise1, noise2, ratioX);
				noiseInt2 = MathUtils.lerp(noise3, noise4, ratioX);
				noise = MathUtils.lerp(noiseInt1, noiseInt2, ratioZ);
				
				for (int y = 0; y < 16; ++y) {
					
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
					} else {
						block.setBlockType(Blocks.AIR);
					}
					
				}
				
			}
		}
		
		/*
		Biome biome, nbiome1, nbiome2;
		float depth, scale, noise;
		byte neighbourBiomes;
		int wx, wy, wz;
		int nx, nz;
		WorldBlock block;
		
		int maxHeight = chunk.getWorld().getWorldHeightLimit();
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				
				wx = pos.getX() + x;
				wz = pos.getZ() + z;
				
				biome = chunk.getSection().getBiomeAtRelative(x, z);
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
				noise = (depth * maxHeight) + (this.surfaceNoise.noise(wx, wz, 0.004f) + 1) * scale;
				
				for (int y = 0; y < 16; ++y) {
					
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
					} else {
						block.setBlockType(Blocks.AIR);
					}
					
				}
				
			}
		}
		*/
		
	}
	
	@Override
	public void genSurface(Section section, SectionPosition pos) {
		// TODO: Generate surface (sand, grass, stone) from biome preferences.
	}
	
	private static long getDecorationSeed(long seed) {
		return seed * 993402349510639L;
	}
	
}
