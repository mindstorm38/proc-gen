package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.WorldBlock;
import fr.theorozier.procgen.world.WorldBlockPosition;
import fr.theorozier.procgen.world.WorldChunk;
import io.msengine.common.util.noise.SeedSimplexNoise;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;

public class BetaChunkGenerator extends ChunkGenerator {
	
	public static final ChunkGeneratorProvider PROVIDER = world -> new BetaChunkGenerator(world.getSeed());
	
	private final SeedSimplexNoise surfaceNoise;
	private final SeedSimplexNoise decorationNoise;
	
	public BetaChunkGenerator(long seed) {
		
		super(seed);
		
		this.surfaceNoise = new SeedSimplexNoise(seed);
		this.decorationNoise = new SeedSimplexNoise(getDecorationSeed(seed));
		
	}
	
	private static long getDecorationSeed(long seed) {
		return seed * 993402349510639L;
	}
	
	@Override
	public void gen(WorldChunk chunk, int cx, int cy, int cz) {
		
		float noise;
		int wx, wy, wz;
		WorldBlock block;
		
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				
				wx = cx + x;
				wz = cz + z;
				
				noise = 30 + noiseAt(this.surfaceNoise, wx, wz, 32, 8, 1.0f, 0.2f, 0);
				
				for (int y = 0; y < CHUNK_SIZE; y++) {
					
					wy = cy + y;
					
					block = chunk.getBlockAtRelative(x, y, z);
					
					if (wy == 0) {
						block.setBlockType(Blocks.BEDROCK);
					} else if (wy < noise - 4) {
						block.setBlockType(Blocks.STONE);
					} else if (wy < noise - 1) {
						block.setBlockType(Blocks.DIRT);
					} else if (wy < noise) {
						block.setBlockType(Blocks.GRASS);
					} else if (wy < noise + 1) {
						
						// Decorate
						noise = noiseAt(this.decorationNoise, wx, wz, 0.7f, 0.05f, 0.05f, 0.05f, 0.15f);
						
						if (noise > 0.8f) {
							this.generateTreeAtRelative(chunk, x, y, z);
						}
						
					} else if (!block.isSet()) {
						block.setBlockType(Blocks.AIR);
					}
					
				}
				
			}
		}
		
		chunk.triggerUpdatedListeners();
		
	}
	
	private void generateTreeAtRelative(WorldChunk chunk, int x, int y, int z) {
		
		WorldBlock block;
		
		int top = y + 4 + MathUtils.fastfloor(this.decorationNoise.noise(x, z));
		
		for (int logY = y; logY < top; logY++) {
			
			if (logY >= CHUNK_SIZE)
				return;
			
			block = chunk.getBlockAtRelative(x, logY, z);
			block.setBlockType(Blocks.LOG);
			
		}
		
		WorldBlockPosition center = new WorldBlockPosition(x, top - 1, z);
		
		for (int leavesY = (top - 2); leavesY < (top + 3); leavesY++) {
			for (int leavesX = (x - 2); leavesX <= (x + 2); leavesX++) {
				for (int leavesZ = (z - 2); leavesZ <= (z + 2); leavesZ++) {
					
					if (leavesY >= top || leavesX != x || leavesZ != z) {
						
						if (center.dist(leavesX, leavesY, leavesZ) <= 2.4f && chunk.isValidRelativePosition(leavesX, leavesY, leavesZ)) {
							
							block = chunk.getBlockAtRelative(leavesX, leavesY, leavesZ);
							block.setBlockType(Blocks.LEAVES, false);
							
						}
						
					}
					
				}
			}
		}
		
	}
	
	/**
	 * Package private method to generate noise for a specific point and parameters.
	 * @param noise The simplex noise to use.
	 * @param x Point X coordinate.
	 * @param y Point Y coordinate.
	 * @param a1 Amplitude for (x,y)/200.
	 * @param a2 Amplitude for (x,y)/100.
	 * @param a3 Amplitude for (x,y)/50.
	 * @param a4 Amplitude for (x,y)/10.
	 * @return Noise at this point.
	 */
	private static float noiseAt(SeedSimplexNoise noise, float x, float y, float a1, float a2, float a3, float a4, float a5) {
		
		return a1 * noise.normnoise(x / 200, y / 200) +
				a2 * noise.normnoise(x / 100, y / 100) +
				a3 * noise.normnoise(x / 50, y / 50) +
				a4 * noise.normnoise(x / 10, y / 10) +
				a5 * noise.normnoise(x / 2, y / 2);
		
	}
	
}
