package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;
import io.msengine.common.util.noise.SeedSimplexNoise;

import java.util.*;
import java.util.function.Consumer;

public class World {
	
	public static final int CHUNK_SIZE         = 16;
	public static final int CHUNK_SIZE_MINUS   = 15;
	public static final int NEAR_CHUNK_LOADING = 16 * 4;
	public static final Block DEFAULT_BLOCK    = Blocks.AIR;
	public static final int MAX_WORLD_HEIGHT   = 256;
	
	private final long seed;
	private final ChunkGenerator generator;
	
	private final Map<WorldBlockPosition, WorldChunk> chunks;
	private final List<WorldChunkLoadedListener> chunkLoadedListeners;
	
	public World(long seed, ChunkGeneratorProvider provider) {
		
		this.seed = seed;
		this.generator = provider.create(this);
		
		this.chunks = new HashMap<>();
		this.chunkLoadedListeners = new ArrayList<>();
		
	}
	
	public World(ChunkGeneratorProvider provider) {
		this(new Random().nextLong(), provider);
	}
	
	/**
	 * @return The world seed.
	 */
	public long getSeed() {
		return this.seed;
	}
	
	/**
	 * Get a chunk absolute position from block position.
	 * @param blockPosition The block position.
	 * @return The chunk position.
	 */
	public WorldBlockPosition getChunkPosition(WorldBlockPosition blockPosition) {
		
		return new WorldBlockPosition(
				blockPosition.getX() - (blockPosition.getX() & CHUNK_SIZE_MINUS),
				blockPosition.getY() - (blockPosition.getY() & CHUNK_SIZE_MINUS),
				blockPosition.getZ() - (blockPosition.getZ() & CHUNK_SIZE_MINUS)
		);
		
	}
	
	/**
	 * Get a chunk absolute position from block position.
	 * @param x The block X position.
	 * @param y The block Y position.
	 * @param z The block Z position.
	 * @return The chunk position.
	 */
	public WorldBlockPosition getChunkPosition(int x, int y, int z) {
		
		return new WorldBlockPosition(
				x - (x & CHUNK_SIZE_MINUS),
				y - (y & CHUNK_SIZE_MINUS),
				z - (z & CHUNK_SIZE_MINUS)
		);
		
	}
	
	/**
	 * Get a chunk at specific position if exists.
	 * @param position The chunk absolute position.
	 * @return The chunk object, or Null if not existing.
	 */
	public WorldChunk getChunkAtAbsolute(WorldBlockPosition position) {
		return this.chunks.get(position);
	}
	
	/**
	 * Get a chunk object from at a block position, if exists.
	 * @param position The block position the get the chunk at.
	 * @return The chunk object, or Null if not existing.
	 */
	public WorldChunk getChunkAt(WorldBlockPosition position) {
		return this.getChunkAtAbsolute(this.getChunkPosition(position));
	}
	
	public WorldChunk getChunkAt(int x, int y, int z) {
		return this.getChunkAtAbsolute(this.getChunkPosition(x, y, z));
	}
	
	/**
	 * Check if a world block position is legal for generation.
	 * @param position The block position to check.
	 * @return True if the game can generate at this position.
	 */
	public boolean canGenerateAt(WorldBlockPosition position) {
		return position.getY() >= 0 && position.getY() <= MAX_WORLD_HEIGHT;
	}
	
	public WorldBlock getBlockAt(WorldBlockPosition position) {
		WorldChunk chunk = this.getChunkAt(position);
		return chunk == null ? null : chunk.getBlockAt(position);
	}
	
	public Block getBlockTypeAt(int x, int y, int z) {
		WorldChunk chunk = this.getChunkAt(x, y, z);
		return chunk == null ? DEFAULT_BLOCK : chunk.getBlockTypeAt(x, y, z);
	}
	
	/**
	 * Force load a chunk at specific block position, if not existing, generate it.
	 * @param position The block position where you want to load the world.
	 * @return The chunk object, or Null if no generation is possible at this
	 *         position (see {@link #canGenerateAt(WorldBlockPosition)}).
	 */
	public WorldChunk loadAt(WorldBlockPosition position) {
		
		if (!this.canGenerateAt(position))
			return null;
	
		WorldBlockPosition chunkpos = this.getChunkPosition(position);
		WorldChunk chunk = this.getChunkAtAbsolute(chunkpos);
		
		if (chunk == null) {
			
			chunk = new WorldChunk(this, chunkpos);
			this.chunks.put(chunkpos, chunk);
			this.generator.gen(chunk, chunkpos.getX(), chunkpos.getY(), chunkpos.getZ());
			
			this.triggerChunkLoadedListeners(chunk);
			
		}
		
		return chunk;
		
	}
	
	public void forEachChunkPosNear(float x, float y, float z, int range, boolean wholeY, Consumer<WorldBlockPosition> consumer) {
		
		WorldBlockPosition chunkPos = this.getChunkPosition(MathUtils.fastfloor(x), MathUtils.fastfloor(y), MathUtils.fastfloor(z));
		WorldBlockPosition minPos = chunkPos.sub(this.getChunkPosition(range, range, range));
		
		int xmax = chunkPos.getX() + range;
		int ymax = wholeY ? MAX_WORLD_HEIGHT : chunkPos.getY() + range;
		int zmax = chunkPos.getZ() + range;
		
		WorldBlockPosition pos;
		
		for (int xv = minPos.getX(); xv <= xmax; xv += CHUNK_SIZE)
			for (int yv = (wholeY ? 0 : minPos.getY()); yv <= ymax; yv += CHUNK_SIZE)
				for (int zv = minPos.getZ(); zv <= zmax; zv += CHUNK_SIZE)
					consumer.accept(new WorldBlockPosition(xv, yv, zv));
					
	}
	
	public void forEachChunkNear(float x, float y, float z, int range, Consumer<WorldChunk> consumer) {
		
		this.forEachChunkPosNear(x, y, z, range, false, pos -> {
			WorldChunk ck = this.getChunkAtAbsolute(pos);
			if (ck != null) consumer.accept(ck);
		});
	
	}
	
	public void loadNear(float x, float y, float z) {
		this.forEachChunkPosNear(x, y, z, NEAR_CHUNK_LOADING, true, this::loadAt);
	}
	
	// CHUNK LOADED LISTENERS //
	
	public void addChunkLoadedListener(WorldChunkLoadedListener l) {
		this.chunkLoadedListeners.add(l);
	}
	
	public void removeChunkLoadedListener(WorldChunkLoadedListener l) {
		this.chunkLoadedListeners.remove(l);
	}
	
	private void triggerChunkLoadedListeners(WorldChunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkLoaded(this, chunk));
	}
	
	private void triggerChunkUnloadedListeners(WorldChunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkUnloaded(this, chunk));
	}
	
}
