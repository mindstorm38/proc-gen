package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;

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
	
	private final Map<BlockPosition, Chunk> chunks;
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
	
	/////////////////////
	// Chunk Accessing //
	/////////////////////
	
	/**
	 * Get a chunk absolute position from block position.
	 * @param blockPosition The block position.
	 * @return The chunk position.
	 */
	public BlockPosition getChunkPosition(BlockPosition blockPosition) {
		
		return new BlockPosition(
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
	public BlockPosition getChunkPosition(int x, int y, int z) {
		
		return new BlockPosition(
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
	public Chunk getChunkAtAbsolute(BlockPosition position) {
		return this.chunks.get(position);
	}
	
	/**
	 * Get a chunk object from at a block position, if exists.
	 * @param position The block position the get the chunk at.
	 * @return The chunk object, or Null if not existing.
	 */
	public Chunk getChunkAt(BlockPosition position) {
		return this.getChunkAtAbsolute(this.getChunkPosition(position));
	}
	
	public Chunk getChunkAt(int x, int y, int z) {
		return this.getChunkAtAbsolute(this.getChunkPosition(x, y, z));
	}
	
	/**
	 * Check if a world block position is legal for generation.
	 * @param position The block position to check.
	 * @return True if the game can generate at this position.
	 */
	public boolean canGenerateAt(BlockPosition position) {
		return position.getY() >= 0 && position.getY() <= MAX_WORLD_HEIGHT;
	}
	
	public WorldBlock getBlockAt(BlockPosition position) {
		Chunk chunk = this.getChunkAt(position);
		return chunk == null ? null : chunk.getBlockAt(position);
	}
	
	public Block getBlockTypeAt(int x, int y, int z) {
		Chunk chunk = this.getChunkAt(x, y, z);
		return chunk == null ? DEFAULT_BLOCK : chunk.getBlockTypeAt(x, y, z);
	}
	
	/**
	 * Force load a chunk at specific block position, if not existing, generate it.
	 * @param position The block position where you want to load the world.
	 * @return The chunk object, or Null if no generation is possible at this
	 *         position (see {@link #canGenerateAt(BlockPosition)}).
	 */
	public Chunk loadAt(BlockPosition position) {
		
		if (!this.canGenerateAt(position))
			return null;
	
		BlockPosition chunkpos = this.getChunkPosition(position);
		Chunk chunk = this.getChunkAtAbsolute(chunkpos);
		
		if (chunk == null) {
			
			chunk = new Chunk(this, chunkpos);
			this.chunks.put(chunkpos, chunk);
			
			this.generator.genBiomes(chunk, chunkpos);
			this.generator.genBase(chunk, chunkpos);
			this.generator.genSurface(chunk, chunkpos);
			this.generator.genFeatures(chunk, chunkpos);
			
			this.triggerChunkLoadedListeners(chunk);
			
		}
		
		return chunk;
		
	}
	
	///////////////
	// Heightmap //
	///////////////
	
	
	
	////////////////////////
	// Position Utilities //
	////////////////////////
	
	public void forEachChunkPosNear(float x, float y, float z, int range, boolean wholeY, Consumer<BlockPosition> consumer) {
		
		BlockPosition chunkPos = this.getChunkPosition(MathUtils.fastfloor(x), MathUtils.fastfloor(y), MathUtils.fastfloor(z));
		BlockPosition minPos = chunkPos.sub(this.getChunkPosition(range, range, range));
		
		int xmax = chunkPos.getX() + range;
		int ymax = wholeY ? MAX_WORLD_HEIGHT : chunkPos.getY() + range;
		int zmax = chunkPos.getZ() + range;
		
		BlockPosition pos;
		
		for (int xv = minPos.getX(); xv <= xmax; xv += CHUNK_SIZE)
			for (int yv = (wholeY ? 0 : minPos.getY()); yv <= ymax; yv += CHUNK_SIZE)
				for (int zv = minPos.getZ(); zv <= zmax; zv += CHUNK_SIZE)
					consumer.accept(new BlockPosition(xv, yv, zv));
					
	}
	
	public void forEachChunkNear(float x, float y, float z, int range, Consumer<Chunk> consumer) {
		
		this.forEachChunkPosNear(x, y, z, range, false, pos -> {
			Chunk ck = this.getChunkAtAbsolute(pos);
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
	
	private void triggerChunkLoadedListeners(Chunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkLoaded(this, chunk));
	}
	
	private void triggerChunkUnloadedListeners(Chunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkUnloaded(this, chunk));
	}
	
}
