package fr.theorozier.procgen.world.chunk;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.Biomes;
import io.msengine.common.osf.OSFObject;

import java.util.*;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;

public class Chunk implements BiomeAccessor {
	
	public static final int CHUNK_SECTION_SURFACE = CHUNK_SIZE * CHUNK_SIZE;
	
	private final World world;
	private final BlockPosition position;
	private final int ex, ey, ez;
	
	private final short[][][] data;
	private final byte[] heightmap;
	private Biome[] biomes;
	
	private ChunkStatus status;
	
	// Metadata for blocks needs them, the key is the block index.
	private final Map<Short, OSFObject> metadata;
	
	private final List<WorldChunkUpdatedListener> updateListeners;
	
	public Chunk(World world, BlockPosition position) {
		
		this.world = world;
		
		this.position = position;
		this.ex = position.getX() + CHUNK_SIZE;
		this.ey = position.getY() + CHUNK_SIZE;
		this.ez = position.getZ() + CHUNK_SIZE;
		
		this.data = new short[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		this.heightmap = new byte[CHUNK_SECTION_SURFACE];
		this.biomes = new Biome[CHUNK_SECTION_SURFACE];
		Arrays.fill(this.biomes, Biomes.EMPTY);
		
		this.status = ChunkStatus.EMPTY;
		
		this.metadata = new HashMap<>();
		
		this.updateListeners = new ArrayList<>();
		
	}
	
	/**
	 * @return The world owning this chunk.
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * @return Chunk position, a multiple of (16, 16, 16).
	 */
	public BlockPosition getChunkPosition() {
		return this.position;
	}
	
	public ChunkStatus getStatus() {
		return this.status;
	}
	
	////////////////////////
	// Position Utilities //
	////////////////////////
	
	/**
	 * Check if this chunk contains a block position.
	 * @param x The point X.
	 * @param y The point Y.
	 * @param z The point Z.
	 * @return True if this point is in this chunk.
	 */
	public boolean validPosition(int x, int y, int z) {
		
		return x >= this.position.getX() && x < this.ex &&
				y >= this.position.getY() && y < this.ey &&
				z >= this.position.getZ() && z < this.ez;
				
	}
	
	/**
	 * Check if this chunk contains a block position.
	 * @param position The block position.
	 * @return True if this block position is in this chunk.
	 */
	public boolean validPosition(BlockPosition position) {
		return this.validPosition(position.getX(), position.getY(), position.getZ());
	}
	
	public void validatePosition(int x, int y, int z) {
		
		if (!this.validPosition(x, y, z))
			throw new IllegalArgumentException("Position out of the chunk range, " + x + "," + y + "," + z + " for chunk at " + this.position + ".");
		
	}
	
	public BlockPosition validatePosition(BlockPosition position) {
		this.validatePosition(position.getX(), position.getY(), position.getZ());
		return position;
	}
	
	public boolean isValidRelativePosition(int x, int y, int z) {
		return x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE && z >= 0 && z < CHUNK_SIZE;
	}
	
	/**
	 * Get the relative position in this chunk from a world position.
	 * @param position The world position.
	 * @return The relative position in this chunk.
	 */
	public BlockPosition getRelativePosition(BlockPosition position) {
		return this.validatePosition(position).sub(this.position);
	}
	
	/**
	 * Get the relative position in this chunk from a world position.
	 * @param x The world position X.
	 * @param y The world position Y.
	 * @param z The world position Z.
	 * @return The relative position in this chunk.
	 */
	public BlockPosition getRelativePosition(int x, int y, int z) {
		this.validatePosition(x, y, z);
		return new BlockPosition(x - this.position.getX(), y - this.position.getY(), z - this.position.getZ());
	}
	
	///////////////
	// Heightmap //
	///////////////
	
	public void setHeightAtRelative(int x, int z, byte heightY) {
		this.heightmap[this.getHorizontalPositionIndex(x, z)] = heightY;
	}
	
	public byte getHeightAtRelative(int x, int z) {
		return this.heightmap[this.getHorizontalPositionIndex(x, z)];
	}
	
	public byte getHeightAtRelative(BlockPosition pos) {
		return this.getHeightAtRelative(pos.getX(), pos.getZ());
	}
	
	public byte getHeightAt(int x, int z) {
		return this.getHeightAtRelative(this.getRelativePosition(x, 0, z));
	}
	
	/////////////////////
	// Biome Accessing //
	/////////////////////
	
	/**
	 * Set the array of biomes.
	 * @param biomes New biomes array, must have a length of {@link #CHUNK_SECTION_SURFACE}.
	 */
	public void setBiomes(Biome[] biomes) {
		
		if (biomes.length != CHUNK_SECTION_SURFACE)
			throw new IllegalArgumentException("Invalid biomes array length, must have chunk section surface length (" + CHUNK_SECTION_SURFACE + ").");
		
		this.biomes = biomes;
		
	}
	
	/**
	 * Get biome at relative position in this chunk.
	 * @param x The X relative position.
	 * @param z The Y relative position.
	 * @return The biome at this block position.
	 */
	public Biome getBiomeAtRelative(int x, int z) {
		return this.biomes[this.getHorizontalPositionIndex(x, z)];
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		BlockPosition rel = this.getRelativePosition(x, 0, z);
		return this.getBiomeAtRelative(rel.getX(), rel.getZ());
	}
	
	@Override
	public Biome getBiomeAt(BlockPosition pos) {
		BlockPosition rel = this.getRelativePosition(pos);
		return this.getBiomeAtRelative(rel.getX(), rel.getZ());
	}
	
	/////////////////////
	// Block Accessing //
	/////////////////////
	
	/**
	 * Get the world block at specific position.
	 * @param position The position of the block.
	 * @return An interface to the block data.
	 */
	public WorldBlock getBlockAt(BlockPosition position) {
		
		this.validatePosition(position);
		return new WorldBlock(this, position, this.getRelativePosition(position));
		
	}
	
	public WorldBlock getBlockAtRelative(int x, int y, int z) {
		return new WorldBlock(this, new BlockPosition(this.position.getX() + x, this.position.getY() + y, this.position.getZ() + z), new BlockPosition(x, y, z));
	}
	
	/**
	 * Get the world block type at absolute world position.
	 * @param x The block X position.
	 * @param y The block Y position.
	 * @param z The block Z position.
	 * @return The block type.
	 */
	public Block getBlockTypeAt(int x, int y, int z) {
		
		this.validatePosition(x, y, z);
		return this.getBlockTypeAtRelative(x - this.position.getX(), y - this.position.getY(), z - this.position.getZ());
		
	}
	
	/**
	 * Check if there is a specific block at block position.
	 * @param x The block X position.
	 * @param y The block Y position.
	 * @param z The block Z position.
	 * @return True if there is a block.
	 */
	public boolean hasBlockAtRelative(int x, int y, int z) {
		return this.data[x][y][z] != 0;
	}
	
	/**
	 * Internal method for block interface {@link WorldBlock} to get its type.
	 * @param x Block relative X.
	 * @param y Block relative Y.
	 * @param z Block relative Z.
	 * @return The block type (constants from {@link Blocks}), AIR by default if no block.
	 */
	public Block getBlockTypeAtRelative(int x, int y, int z) {
		
		Block b = Blocks.getBlock(this.data[x][y][z]);
		return b == null ? World.DEFAULT_BLOCK : b;
		
	}
	
	/**
	 * Internal method for block interface {@link WorldBlock} to set its type.
	 * @param x Block relative X.
	 * @param y Block relative Y.
	 * @param z Block relative Z.
	 * @param block The block to set at this position.
	 */
	void setBlockTypeAtRelative(int x, int y, int z, Block block, boolean overwrite) {
		
		if (this.data[x][y][z] != 0) {
			
			if (!overwrite)
				return;
			
			this.removeBlockMetadataAt(this.getPositionIndex(x, y, z));
			
		}
		
		if (block == null || block.isUnsavable()) {
			
			this.data[x][y][z] = 0;
			
		} else {
			
			this.data[x][y][z] = block.getUid();
			block.initBlock(this.getBlockAtRelative(x, y, z));
			
		}
		
	}
	
	/**
	 * Internal method to get the position index in a 3D linear array of cube size {@link World#CHUNK_SIZE}.
	 * @param x Relative position X.
	 * @param y Relative position Y.
	 * @param z Relative position Z.
	 * @return Position index.
	 */
	short getPositionIndex(int x, int y, int z) {
		return (short) (x * CHUNK_SIZE * CHUNK_SIZE + y * CHUNK_SIZE + z);
	}
	
	/**
	 * Internal method to get the position index in a 2D linear array of square size {@link World#CHUNK_SIZE}.
	 * @param x Relative position X.
	 * @param z Relative position Z.
	 * @return Position index.
	 */
	short getHorizontalPositionIndex(int x, int z) {
		return (short) (x * CHUNK_SIZE + z);
	}
	
	////////////////////
	// Block Metadata //
	////////////////////
	
	OSFObject getBlockMetadataAt(short index, boolean create) {
		
		if (create && !this.metadata.containsKey(index))
			this.metadata.put(index, new OSFObject());
			
		return this.metadata.get(index);
		
	}
	
	void removeBlockMetadataAt(short index) {
		this.metadata.remove(index);
	}
	
	/**
	 * Iterate over each not empty block (data not set to 0).
	 * @param consumer The special consumer used to iterate.
	 */
	public void forEachNotEmptyBlock(NotEmptyBlockConsumer consumer) {
		
		for (int x = 0; x < CHUNK_SIZE; x++)
			for (int y = 0; y < CHUNK_SIZE; y++)
				for (int z = 0; z < CHUNK_SIZE; z++)
					if (this.data[x][y][z] != 0)
						consumer.accept(x, y, z, this.getBlockTypeAtRelative(x, y, z));
	
	}
	
	public interface NotEmptyBlockConsumer {
		void accept(int relx, int rely, int relz, Block block);
	}
	
	public void addUpdatedListener(WorldChunkUpdatedListener l) {
		this.updateListeners.add(l);
	}
	
	public void removeUpdatedListener(WorldChunkUpdatedListener l) {
		this.updateListeners.remove(l);
	}
	
	public void triggerUpdatedListeners() {
		this.updateListeners.forEach(l -> l.worldChunkUpdated(this));
	}
	
	public void triggerUpdatedListenersAt(int x, int y, int z, Block block) {
		this.updateListeners.forEach(l -> l.worldChunkUpdated(this, x, y, z, block));
	}
	
}
