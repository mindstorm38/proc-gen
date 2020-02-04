package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import io.msengine.common.util.noise.OctaveSimplexNoise;

public class Beta3DChunkGenerator extends ChunkGenerator {
	
	private final OctaveSimplexNoise baseNoise;
	
	private final int noiseHorizontalGranularity;
	private final int noiseVerticalGranularity;
	
	private final int noiseHorizontalCount;
	private final int noiseVerticalCount;
	
	public Beta3DChunkGenerator(long seed) {
		
		super(seed, new BetaBiomeProvider(seed));
		
		this.baseNoise = new OctaveSimplexNoise(seed, 12, 0.6f, 1.5f);
		
		this.noiseHorizontalGranularity = 8; // TODO Add these fields to arguments
		this.noiseVerticalGranularity = 8;
		
		if (!MathUtils.isPowerOfTwo(this.noiseHorizontalGranularity))
			throw new IllegalArgumentException("Invalid noise horizontal granulatity, must be a power of 2.");
		
		if (!MathUtils.isPowerOfTwo(this.noiseVerticalGranularity))
			throw new IllegalArgumentException("Invalid noise vertical granulatity, must be a power of 2.");
		
		this.noiseHorizontalCount = 16 / this.noiseHorizontalGranularity;
		this.noiseVerticalCount = 16 / this.noiseVerticalGranularity;
		
	}
	
	@Override
	public void genBase(WorldServerChunk chunk, BlockPositioned pos) {
		
		int chunkX = pos.getX() << 4;
		int chunkY = pos.getY() << 4;
		int chunkZ = pos.getZ() << 4;
		
		WorldServer world = chunk.getWorld();
		int maxHeight = world.getHeightLimit();
		int seaLevel = world.getSeaLevel();
		
		int realX, realY, realZ;
		
		Biome biome;
		float depthHeight;
		float realScale;
		float weight;
		
		float[][][] noiseMap = new float[this.noiseHorizontalCount + 1][this.noiseHorizontalCount + 1][this.noiseVerticalCount + 1];
		
		for (int x = 0; x <= this.noiseHorizontalCount; ++x) {
			for (int z = 0; z <= this.noiseHorizontalCount; ++z) {
				
				realX = chunkX + x * this.noiseHorizontalGranularity;
				realZ = chunkZ + z * this.noiseHorizontalGranularity;
				
				if (x == this.noiseHorizontalCount || z == this.noiseHorizontalCount) {
					biome = this.biomeProvider.getBiomeAt(realX, realZ);
				} else {
					biome = chunk.getBiomeAt(x * this.noiseHorizontalGranularity, z * this.noiseHorizontalGranularity);
				}
				
				depthHeight = biome.getDepth() * maxHeight;
				
				for (int y = 0; y <= this.noiseVerticalCount; ++y) {
					
					realY = chunkY + y * this.noiseVerticalGranularity;
					
					realScale = biome.getScale();
					
					if (realY > depthHeight)
						realScale *= 2;
					
					weight = 2f / (1f + (float) Math.exp((realY - depthHeight) / realScale)) - 1f; // Sigmoid
					noiseMap[x][z][y] = (this.baseNoise.noise(realX, realY, realZ, 0.01f) + 1f) / 2f + weight;
					
				}
				
			}
		}
		
		int noiseX, noiseZ, noiseY;
		int relX, relZ, relY;
		float ratioX, ratioZ, ratioY;
		float n1, n2, n01, n02, n001, n002, noise;
		
		for (int x = 0; x < 16; ++x) {
			
			noiseX = x / this.noiseHorizontalGranularity;
			relX = x % this.noiseHorizontalGranularity;
			ratioX = relX / (float) this.noiseHorizontalGranularity;
			
			for (int z = 0; z < 16; ++z) {
				
				noiseZ = z / this.noiseHorizontalGranularity;
				relZ = z % this.noiseHorizontalGranularity;
				ratioZ = relZ / (float) this.noiseHorizontalGranularity;
				
				for (int y = 0; y < 16; ++y) {
					
					noiseY = y / this.noiseVerticalGranularity;
					relY = y % this.noiseVerticalGranularity;
					ratioY = relY / (float) this.noiseVerticalGranularity;
					
					// Bottom plate
					n1 = noiseMap[noiseX    ][noiseZ][noiseY];
					n2 = noiseMap[noiseX + 1][noiseZ][noiseY];
					n01 = MathUtils.lerp(n1, n2, ratioX);
					
					n1 = noiseMap[noiseX    ][noiseZ + 1][noiseY];
					n2 = noiseMap[noiseX + 1][noiseZ + 1][noiseY];
					n02 = MathUtils.lerp(n1, n2, ratioX);
					
					n001 = MathUtils.lerp(n01, n02, ratioZ);
					
					// Top plate
					n1 = noiseMap[noiseX    ][noiseZ][noiseY + 1];
					n2 = noiseMap[noiseX + 1][noiseZ][noiseY + 1];
					n01 = MathUtils.lerp(n1, n2, ratioX);
					
					n1 = noiseMap[noiseX    ][noiseZ + 1][noiseY + 1];
					n2 = noiseMap[noiseX + 1][noiseZ + 1][noiseY + 1];
					n02 = MathUtils.lerp(n1, n2, ratioX);
					
					n002 = MathUtils.lerp(n01, n02, ratioZ);
					
					// Final noise
					noise = MathUtils.lerp(n001, n002, ratioY);
					
					if (noise > 0.5f) {
						chunk.setBlockAt(x, y, z, Blocks.STONE.getDefaultState());
					} else if ((chunkY + y) < seaLevel) {
						chunk.setBlockAt(x, y, z, Blocks.WATER.getDefaultState());
					}
					
				}
				
			}
			
		}
		
		/*
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				
				biome = chunk.getBiomeAt(x, z);
				depthHeight = biome.getDepth() * maxHeight;
				
				realX = chunkX + x;
				realZ = chunkZ + z;
				
				for (int y = 0; y < 16; ++y) {
					
					realY = chunkY + y;
					
					realScale = biome.getScale();
					
					if (realY > depthHeight)
						realScale *= 2;
					
					weight = 2f / (1f + (float) Math.exp((realY - depthHeight) / realScale)) - 1f; // Sigmoid
					noise = (this.baseNoise.noise(realX, realY, realZ, 0.01f) + 1f) / 2f + weight;
					
					if (noise > 0.5f) {
						chunk.setBlockAt(x, y, z, Blocks.STONE.getDefaultState());
					} else if ((chunkY + y) < seaLevel) {
						chunk.setBlockAt(x, y, z, Blocks.WATER.getDefaultState());
					}
				
				}
				
			}
		}
		*/
		
	}
	
}
