package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import io.msengine.common.util.noise.OctaveSimplexNoise;
import io.sutil.math.MathHelper;

public class BetaChunkGenerator extends ChunkGenerator {
	
	private static final BlockState BEDROCK_STATE = Blocks.BEDROCK.getDefaultState();
	private static final BlockState STONE_STATE = Blocks.STONE.getDefaultState();
	private static final BlockState WATER_STATE = Blocks.WATER.getDefaultState();
	
	// Class //
	
	private final OctaveSimplexNoise baseNoise;
	
	private final int noiseHorizontalGranularity;
	private final int noiseHorizontalSize;
	
	public BetaChunkGenerator(long seed) {
		
		super(seed, new BetaBiomeProvider(seed));
		
		this.baseNoise = new OctaveSimplexNoise(seed, 16, 0.6f, 2.0f);
		
		this.noiseHorizontalGranularity = 8;
		this.noiseHorizontalSize = 16 / this.noiseHorizontalGranularity;
		
	}
	
	@Override
	public void genBase(WorldServerChunk chunk, BlockPositioned pos) {
		
		float[][] randomMap = new float[this.noiseHorizontalSize + 1][this.noiseHorizontalSize + 1];
		
		int chunkX = pos.getX() << 4;
		int chunkY = pos.getY() << 4;
		int chunkZ = pos.getZ() << 4;
		WorldServer world = chunk.getWorld();
		
		int maxHeight = world.getHeightLimit();
		Biome biome;
		
		for (int x = 0; x <= this.noiseHorizontalSize; ++x) {
			for (int z = 0; z <= this.noiseHorizontalSize; ++z) {
				
				if (x == this.noiseHorizontalSize || z == this.noiseHorizontalSize) {
					biome = this.biomeProvider.getBiomeAt(chunkX + x * this.noiseHorizontalGranularity, chunkZ + z * this.noiseHorizontalGranularity);
				} else {
					biome = chunk.getBiomeAt(x * this.noiseHorizontalGranularity, z * this.noiseHorizontalGranularity);
				}
				
				randomMap[x][z] = (biome.getDepth() * maxHeight) +
						this.baseNoise.noise(
								pos.getX() * this.noiseHorizontalSize + x,
								pos.getZ() * this.noiseHorizontalSize + z,
								0.04f
						) * biome.getScale();
			
			}
		}
		
		int noiseX, noiseZ, relativeX, relativeZ;
		float ratioX, ratioZ;
		float noise1, noise2, noise3, noise4;
		float noiseInt1, noiseInt2, noise;
		
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
				
				noiseInt1 = MathHelper.interpolate(ratioX, noise2, noise1);
				noiseInt2 = MathHelper.interpolate(ratioX, noise4, noise3);
				noise = MathHelper.interpolate(ratioZ, noiseInt2, noiseInt1);
				
				for (int y = 0; y < 16; ++y) {
					
					wy = chunkY + y;
					
					if (wy == 0) {
						chunk.setBlockAt(x, y, z, BEDROCK_STATE);
					} else if (wy < noise) {
						chunk.setBlockAt(x, y, z, STONE_STATE);
					} else if (wy < world.getSeaLevel()) {
						chunk.setBlockAt(x, y, z, WATER_STATE);
					}
					
				}
				
			}
		}
		
	}
	
}
