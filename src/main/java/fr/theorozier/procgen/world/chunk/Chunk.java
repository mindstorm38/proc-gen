package fr.theorozier.procgen.world.chunk;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.BiomeAccessor;
import io.msengine.common.osf.OSFObject;

import java.util.*;
import java.util.function.Consumer;

public class Chunk implements BiomeAccessor {
	
	private final World world;
	private final Section section;
	private final BlockPosition position;
	private final int ex, ey, ez;
	
	private final short[][][] data;
	private int blocksCount;
	
	private ChunkStatus status;
	
	// Metadata for blocks needs them, the key is the block index.
	private final Map<Short, OSFObject> metadata;
	
	private final List<WorldChunkUpdatedListener> updateListeners;
	
	public Chunk(Section section, BlockPosition position) {
		
		this.world = section.getWorld();
		this.section = section;
		
		this.position = position;
		this.ex = position.getX() + 16;
		this.ey = position.getY() + 16;
		this.ez = position.getZ() + 16;
		
		this.data = new short[16][16][16];
		this.blocksCount = 0;
		
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
	 * @return The section where this chunk is.
	 */
	public Section getSection() {
		return this.section;
	}
	
	/**
	 * @return Chunk position, a multiple of (16, 16, 16).
	 */
	public BlockPosition getChunkPosition() {
		return this.position;
	}
	
	/**
	 * Compute the squared distance to the center of this chunk from an other point.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The squared distance to the center of this chunk.
	 */
	public float getDistanceSquaredTo(float x, float y, float z) {
		return this.position.distSquared(x + 7, y + 7, z + 7);
	}
	
	public ChunkStatus getStatus() {
		return this.status;
	}
	
	public int getBlocksCount() {
		return this.blocksCount;
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
		return x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16;
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
	
	public void checkBlockOnFaces(int x, int y, int z, Consumer<Direction> consumer) {
		
		int rx = x - this.position.getX();
		int ry = y - this.position.getY();
		int rz = z - this.position.getZ();
		
		if (rx == 0) consumer.accept(Direction.SOUTH);
		else if (rx == 15) consumer.accept(Direction.NORTH);
		
		if (ry == 0) consumer.accept(Direction.BOTTOM);
		else if (ry == 15) consumer.accept(Direction.TOP);
		
		if (rz == 0) consumer.accept(Direction.WEST);
		else if (rz == 15) consumer.accept(Direction.EAST);
		
	}
	
	/////////////////////
	// Biome Accessing //
	/////////////////////
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		return this.section.getBiomeAt(x, z);
	}
	
	@Override
	public Biome getBiomeAt(SectionPosition pos) {
		return this.section.getBiomeAt(pos);
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
			
			this.removeBlockMetadataAt(getPositionIndex(x, y, z));
			this.blocksCount--;
			
		}
		
		if (block == null || block.isUnsavable()) {
			
			this.data[x][y][z] = 0;
			
		} else {
			
			this.data[x][y][z] = block.getUid();
			block.initBlock(this.getBlockAtRelative(x, y, z));
			this.blocksCount++;
			
		}
		
	}
	
	/**
	 * Internal method to get the position index in a 3D linear array of cube size 16.
	 * @param x Relative position X.
	 * @param y Relative position Y.
	 * @param z Relative position Z.
	 * @return Position index.
	 */
	public static short getPositionIndex(int x, int y, int z) {
		return (short) (x * 16 * 16 + y * 16 + z);
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
		
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++)
				for (int z = 0; z < 16; z++)
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
