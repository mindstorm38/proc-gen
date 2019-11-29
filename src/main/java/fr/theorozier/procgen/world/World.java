package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.Section;
import fr.theorozier.procgen.world.chunk.SectionPosition;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;

import java.util.*;
import java.util.function.Consumer;

public class World {
	
	public static final int NEAR_CHUNK_LOADING = 16 * 4;
	public static final Block DEFAULT_BLOCK    = Blocks.AIR;
	public static final int MAX_SECTION_HEIGHT = 16;
	
	private final long seed;
	private final ChunkGenerator generator;
	
	private final Map<SectionPosition, Section> sections;
	
	// private final Map<BlockPosition, Chunk> chunks;
	private final List<WorldChunkLoadedListener> chunkLoadedListeners;
	
	public World(long seed, ChunkGeneratorProvider provider) {
		
		this.seed = seed;
		this.generator = provider.create(this);
		
		this.sections = new HashMap<>();
		
		// this.chunks = new HashMap<>();
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
	
	//////////////////
	// Height Limit //
	//////////////////
	
	/**
	 * Get max section height of chunks.
	 * @return Max numbers of check in the height of a section.
	 */
	public int getSectionHeightLimit() {
		return MAX_SECTION_HEIGHT;
	}
	
	/**
	 * Get max world height.
	 * @return Max blocks height of the world.
	 */
	public int getWorldHeightLimit() {
		return MAX_SECTION_HEIGHT * 16;
	}
	
	/**
	 * Check if a position Y is valid for height limits.
	 * @param y Position Y component.
	 * @return True if the height if valid.
	 */
	public boolean isValidWorldHeightPosition(int y) {
		return y >= 0 && y < this.getWorldHeightLimit();
	}
	
	/**
	 * Check if a position Y is valid for height limits, if no throwing {@link IllegalArgumentException}.
	 * @param y Position Y component.
	 * @throws IllegalArgumentException If the y value is invalid.
	 */
	public void validateWorldHeightPosition(int y) {
		
		if (!this.isValidWorldHeightPosition(y))
			throw new IllegalArgumentException("Invalid Y position for this world, must be less than " + this.getWorldHeightLimit());
		
	}
	
	/**
	 * Check if a world block position is legal for generation.
	 * @param position The block position to check.
	 * @return True if the game can generate at this position.
	 */
	public boolean canGenerateAt(BlockPosition position) {
		return position.getY() >= 0 && position.getY() <= this.getWorldHeightLimit();
	}
	
	///////////////////////
	// Section Accessing //
	///////////////////////
	
	public static SectionPosition getSectionPosition(int x, int z) {
		return new SectionPosition(x >> 4 << 4, z >> 4 << 4);
	}
	
	public static SectionPosition getSectionPosition(SectionPosition pos) {
		return getSectionPosition(pos.getX(), pos.getZ());
	}
	
	public static SectionPosition getSectionPosition(BlockPosition pos) {
		return getSectionPosition(pos.getX(), pos.getZ());
	}
	
	public Section getSectionAtAbsolute(SectionPosition pos) {
		return this.sections.get(pos);
	}
	
	public Section getSectionAt(int x, int z) {
		return this.getSectionAtAbsolute(getSectionPosition(x, z));
	}
	
	public Section getSectionAt(SectionPosition pos) {
		return this.getSectionAtAbsolute(getSectionPosition(pos));
	}
	
	public Section getSectionAt(BlockPosition pos) {
		return this.getSectionAtAbsolute(getSectionPosition(pos));
	}
	
	/////////////////////
	// Chunk Accessing //
	/////////////////////
	
	/**
	 * Get a chunk absolute position from block position.
	 * @param x The block X position.
	 * @param y The block Y position.
	 * @param z The block Z position.
	 * @return The chunk position.
	 */
	public static BlockPosition getChunkPosition(int x, int y, int z) {
		return new BlockPosition(x >> 4 << 4, y >> 4 << 4, z >> 4 << 4);
	}
	
	/**
	 * Get a chunk absolute position from block position.
	 * @param pos The block position.
	 * @return The chunk position.
	 */
	public static BlockPosition getChunkPosition(BlockPosition pos) {
		return getChunkPosition(pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Faster method than given a block position, this method require the exact X,Z,Y coordinates.
	 * @param position Chunk origin position.
	 * @return The chunk if existing.
	 */
	public Chunk getChunkAtAbsolute(BlockPosition position) {
		Section section = this.getSectionAtAbsolute(position.toSectionPosition());
		return section == null ? null : section.getChunkAt(position.getY());
	}
	
	/**
	 * Get a chunk object from at a block position, if exists.
	 * @param position The block position the get the chunk at.
	 * @return The chunk object, or Null if not existing.
	 */
	public Chunk getChunkAt(BlockPosition position) {
		Section section = this.getSectionAt(position);
		return section == null ? null : section.getChunkAt(position.getY());
	}
	
	public Chunk getChunkAt(int x, int y, int z) {
		Section section = this.getSectionAt(x, z);
		return section == null ? null : section.getChunkAt(y);
	}
	
	public WorldBlock getBlockAt(BlockPosition position) {
		Chunk chunk = this.getChunkAt(position);
		return chunk == null ? null : chunk.getBlockAt(position);
	}
	
	public Block getBlockTypeAt(int x, int y, int z) {
		Chunk chunk = this.getChunkAt(x, y, z);
		return chunk == null ? DEFAULT_BLOCK : chunk.getBlockTypeAt(x, y, z);
	}
	
	/*
	 * Force load a chunk at specific block position, if not existing, generate it.
	 * @param position The block position where you want to load the world.
	 * @return The chunk object, or Null if no generation is possible at this
	 *         position (see {@link #canGenerateAt(BlockPosition)}).
	 */
	/*
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
	*/
	
	////////////////
	// Generation //
	////////////////
	
	/**
	 * Try to load a section.
	 * @param position The absolute position of the section.
	 * @return Loaded section.
	 */
	public Section loadSection(SectionPosition position) {
		
		Section section = this.getSectionAtAbsolute(position);
		
		if (section == null) {
			
			section = new Section(this, position, this.generator);
			this.sections.put(position, section);
			
			section.generate();
			
		}
		
		return section;
		
	}
	
	///////////////
	// Heightmap //
	///////////////
	
	
	
	////////////////////////
	// Position Utilities //
	////////////////////////
	
	public void forEachChunkPosNear(float x, float y, float z, int range, boolean wholeY, Consumer<BlockPosition> consumer) {
		
		BlockPosition chunkPos = getChunkPosition(MathUtils.fastfloor(x), MathUtils.fastfloor(y), MathUtils.fastfloor(z));
		BlockPosition minPos = chunkPos.sub(getChunkPosition(range, range, range));
		
		int xmax = chunkPos.getX() + range;
		int ymax = wholeY ? this.getWorldHeightLimit() : chunkPos.getY() + range;
		int zmax = chunkPos.getZ() + range;
		
		for (int xv = minPos.getX(); xv <= xmax; xv += 16)
			for (int yv = (wholeY ? 0 : minPos.getY()); yv <= ymax; yv += 16)
				for (int zv = minPos.getZ(); zv <= zmax; zv += 16)
					consumer.accept(new BlockPosition(xv, yv, zv));
					
	}
	
	public void forEachChunkNear(float x, float y, float z, int range, Consumer<Chunk> consumer) {
		
		this.forEachChunkPosNear(x, y, z, range, false, pos -> {
			Chunk ck = this.getChunkAtAbsolute(pos);
			if (ck != null) consumer.accept(ck);
		});
	
	}
	
	public void forEachSectionPosNear(float x, float z, int range, Consumer<SectionPosition> consumer) {
		
		SectionPosition sectionPos = getSectionPosition(MathUtils.fastfloor(x), MathUtils.fastfloor(z));
		SectionPosition minPos = sectionPos.sub(getSectionPosition(range, range));
		
		int xmax = sectionPos.getX() + range;
		int zmax = sectionPos.getZ() + range;
		
		for (int xv = minPos.getX(); xv <= xmax; xv += 16)
			for (int zv = minPos.getZ(); zv <= zmax; zv += 16)
				consumer.accept(new SectionPosition(xv, zv));
			
	}
	
	public void loadNear(float x, float z) {
		this.forEachSectionPosNear(x, z, NEAR_CHUNK_LOADING, this::loadSection);
	}
	
	// CHUNK LOADED LISTENERS //
	
	public void addChunkLoadedListener(WorldChunkLoadedListener l) {
		this.chunkLoadedListeners.add(l);
	}
	
	public void removeChunkLoadedListener(WorldChunkLoadedListener l) {
		this.chunkLoadedListeners.remove(l);
	}
	
	public void triggerChunkLoadedListeners(Chunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkLoaded(this, chunk));
	}
	
	public void triggerChunkUnloadedListeners(Chunk chunk) {
		this.chunkLoadedListeners.forEach(l -> l.worldChunkUnloaded(this, chunk));
	}
	
}
