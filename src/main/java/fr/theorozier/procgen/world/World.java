package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.BiomeAccessor;
import fr.theorozier.procgen.world.chunk.*;
import fr.theorozier.procgen.world.gen.ChunkGenerator;
import fr.theorozier.procgen.world.gen.ChunkGeneratorProvider;
import fr.theorozier.procgen.world.tick.WorldTickEntry;
import fr.theorozier.procgen.world.tick.WorldTickList;

import java.util.*;
import java.util.function.Consumer;

public class World implements BiomeAccessor {
	
	public static final int NEAR_CHUNK_LOADING = 16 * 4;
	public static final Block DEFAULT_BLOCK    = Blocks.AIR;
	public static final int MAX_SECTION_HEIGHT = 16;
	
	private final long seed;
	private final Random rand;
	private final ChunkGenerator generator;
	private final short seaLevel;
	
	private final Map<SectionPosition, Section> sections;
	private final List<WorldChunkLoadedListener> chunkLoadedListeners;
	
	private final WorldTickList<Block> blockTickList;
	private long time;
	
	public World(long seed, ChunkGeneratorProvider provider) {
		
		this.seed = seed;
		this.rand = new Random(this.seed);
		this.generator = provider.create(this);
		this.seaLevel = 63;
		
		this.sections = new HashMap<>();
		this.chunkLoadedListeners = new ArrayList<>();
		
		this.blockTickList = new WorldTickList<>(this, Block::isTickable, this::tickBlock);
		this.time = 0L;
		
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
	 * @return This world dynamic random, not the one for world generation,
	 * but for example spawning creatures.
	 */
	public Random getRandom() {
		return this.rand;
	}
	
	/**
	 * @return Current world game time.
	 */
	public long getTime() {
		return this.time;
	}
	
	/**
	 * @return This world sea level.
	 */
	public int getSeaLevel() {
		return this.seaLevel;
	}
	
	////////////////////
	// Update & Ticks //
	////////////////////
	
	/**
	 * Run a single tick in the world.
	 */
	public void update() {
		
		this.time++;
		this.blockTickList.tick();
		
	}
	
	/**
	 * Check if a tick can be triggered at a position.
	 * @param pos The position to check.
	 * @return True if you can tick at this position.
	 */
	public boolean canTickAt(BlockPosition pos) {
		return this.getChunkAt(pos) != null;
	}
	
	public WorldTickList<Block> getBlockTickList() {
		return this.blockTickList;
	}
	
	/**
	 * Internal method to tick a block.
	 * @param entry The tick entry from tick list.
	 */
	private void tickBlock(WorldTickEntry<Block> entry) {
	
		WorldBlock wb = this.getBlockAt(entry.getPosition());
		
		if (wb.getBlockType() == entry.getTarget()) {
			entry.getTarget().tickBlock(this, wb, this.rand);
		}
	
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
	
	public static SectionPosition getSectionPosition(HorizontalPosition pos) {
		return getSectionPosition(pos.getX(), pos.getZ());
	}
	
	public Section getSectionAtAbsolute(SectionPosition pos) {
		return this.sections.get(pos);
	}
	
	public Section getSectionAt(int x, int z) {
		return this.getSectionAtAbsolute(getSectionPosition(x, z));
	}
	
	public Section getSectionAt(HorizontalPosition pos) {
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
	
	public WorldBlock getBlockAt(int x, int y, int z) {
		Chunk chunk = this.getChunkAt(x, y, z);
		return chunk == null ? null : chunk.getBlockAt(x, y, z);
	}
	
	public Block getBlockTypeAt(int x, int y, int z) {
		Chunk chunk = this.getChunkAt(x, y, z);
		return chunk == null ? DEFAULT_BLOCK : chunk.getBlockTypeAt(x, y, z);
	}
	
	public Block getBlockTypeAt(BlockPosition position) {
		return this.getBlockTypeAt(position.getX(), position.getY(), position.getZ());
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		Section section = this.getSectionAt(x, z);
		return section == null ? null : section.getBiomeAt(x, z);
	}
	
	@Override
	public Biome getBiomeAt(SectionPosition pos) {
		Section section = this.getSectionAt(pos);
		return section == null ? null : section.getBiomeAt(pos);
	}
	
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
	
	public short getHeightAt(Heightmap.Type type, int x, int z) {
		Section section = this.getSectionAt(x, z);
		return section == null ? 0 : section.getHeightAt(type, x, z);
	}
	
	public short getHeightAt(Heightmap.Type type, HorizontalPosition pos) {
		Section section = this.getSectionAt(pos);
		return section == null ? 0 : section.getHeightAt(type, pos);
	}
	
	public BlockPosition getBlockHeightAt(Heightmap.Type type, HorizontalPosition pos) {
		return new BlockPosition(pos, this.getHeightAt(type, pos));
	}
	
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
