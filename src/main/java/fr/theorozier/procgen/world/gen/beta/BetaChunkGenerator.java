package fr.theorozier.procgen.world.gen.beta;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;
import io.msengine.common.util.noise.OctaveSimplexNoise;

public class BetaChunkGenerator extends ChunkGenerator {
	
	public static final ChunkGeneratorProvider PROVIDER = world -> new BetaChunkGenerator(world.getSeed());
	
	// Class //
	
	private final OctaveSimplexNoise baseNoise;
	
	private final int noiseHorizontalGranularity;
	private final int noiseHorizontalSize;
	
	public BetaChunkGenerator(long seed) {
		
		super(seed, new BetaBiomeProvider(seed));
		
		this.baseNoise = new OctaveSimplexNoise(seed, 16, 0.4f, 2.0f);
		
		this.noiseHorizontalGranularity = 4;
		this.noiseHorizontalSize = 16 / this.noiseHorizontalGranularity;
		
	}
	
	@Override
	public void genBase(Chunk chunk, BlockPosition pos) {
		
		float[][] randomMap = new float[this.noiseHorizontalSize + 1][this.noiseHorizontalSize + 1];
		
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;
		World world = chunk.getWorld();
		
		int maxHeight = chunk.getWorld().getWorldHeightLimit();
		Biome biome;
		
		for (int x = 0; x <= this.noiseHorizontalSize; ++x) {
			for (int z = 0; z <= this.noiseHorizontalSize; ++z) {
				
				if (x == this.noiseHorizontalSize || z == this.noiseHorizontalSize) {
					biome = this.biomeProvider.getBiomeAt(pos.getX() + x * this.noiseHorizontalGranularity, pos.getZ() + z * this.noiseHorizontalGranularity);
				} else {
					biome = chunk.getSection().getBiomeAtRelative(x * this.noiseHorizontalGranularity, z * this.noiseHorizontalGranularity);
				}
				
				randomMap[x][z] = (biome.getDepth() * maxHeight) +
						this.baseNoise.noise(
								chunkX * this.noiseHorizontalSize + x,
								chunkZ * this.noiseHorizontalSize + z,
								0.04f
						) * biome.getScale();
			
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
					} else if (wy < noise) {
						block.setBlockType(Blocks.STONE);
					} else if (wy < world.getSeaLevel()) {
						block.setBlockType(Blocks.WATER);
					}
					
				}
				
			}
		}
		
	}
	
}
