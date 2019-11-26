package fr.theorozier.procgen.world.gen;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.WorldBlock;
import fr.theorozier.procgen.world.WorldChunk;
import io.msengine.common.util.noise.SeedSimplexNoise;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;

public class BetaChunkGenerator extends ChunkGenerator {
	
	public static final ChunkGeneratorProvider PROVIDER = world -> new BetaChunkGenerator(world.getSeed());
	
	private final SeedSimplexNoise noise;
	
	public BetaChunkGenerator(long seed) {
		
		super(seed);
		
		this.noise = new SeedSimplexNoise(seed);
		
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
				
				noise = 30 + this.noiseAt(wx, wz, 32, 8, 1.0f, 0.2f);
				
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
						noise = this.noiseAt(wx, wz, 0.6f, 0.2f, 0.0f, 0.2f);
						
						if (noise > 0.8f) {
							this.generateTreeAtRelative(chunk, x, y, z);
						}
						
					} else {
						block.setBlockType(Blocks.AIR);
					}
					
				}
				
			}
		}
		
	}
	
	private void generateTreeAtRelative(WorldChunk chunk, int x, int y, int z) {
		
		WorldBlock root = chunk.getBlockAtRelative(x, y, z);
		root.setBlockType(Blocks.LOG);
		
	}
	
	/**
	 * Package private method to generate noise for a specific point and parameters.
	 * @param x Point X coordinate.
	 * @param y Point Y coordinate.
	 * @param a1 Amplitude for (x,y)/200.
	 * @param a2 Amplitude for (x,y)/100.
	 * @param a3 Amplitude for (x,y)/50.
	 * @param a4 Amplitude for (x,y)/10.
	 * @return Noise at this point.
	 */
	private float noiseAt(float x, float y, float a1, float a2, float a3, float a4) {
		
		return a1 * this.noise.normnoise(x / 200, y / 200) +
				a2 * this.noise.normnoise(x / 100, y / 100) +
				a3 * this.noise.normnoise(x / 50, y / 50) +
				a4 * this.noise.normnoise(x / 10, y / 10);
		
	}
	
}
