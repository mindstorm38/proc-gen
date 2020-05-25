package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.position.Direction;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 *
 * Object that hold a chunk of (x*y*z) 16*[world height]*16
 * @author Theo Rozier
 *
 */
public class WorldChunk {

	private final WorldBase world;
	private final WorldSection section;
	private final ImmutableBlockPosition position;
	private final ImmutableBlockPosition centerBlockPosition;
	
	private final short[] data;
	
	private final HashSet<Entity> entities;
	
	public WorldChunk(WorldBase world, WorldSection section, ImmutableBlockPosition position) {
		
		this.world = world;
		this.section = section;
		this.position = position;
		this.centerBlockPosition = new ImmutableBlockPosition((position.getX() << 4) + 7, (position.getY() << 4) + 7, (position.getZ() << 4) + 7);
		
		this.data = new short[4096];
		
		this.entities = new HashSet<>();
		
	}
	
	// PROPERTIES //
	
	public WorldBase getWorld() {
		return this.world;
	}
	
	public WorldSection getSection() {
		return this.section;
	}
	
	public final ImmutableBlockPosition getChunkPos() {
		return this.position;
	}
	
	public float getDistSquaredTo(float x, float y, float z) {
		return this.centerBlockPosition.distSquared(x, y, z);
	}
	
	// BIOMES //
	
	public Biome getBiomeAt(int x, int z) {
		return this.section.getBiomeAt(x, z);
	}
	
	public Biome getBiomeAtBlock(int x, int z) {
		return this.section.getBiomeAtBlock(x, z);
	}
	
	public Biome getBiomeAtBlock(SectionPositioned pos) {
		return this.section.getBiomeAtBlock(pos);
	}
	
	// BLOCKS //
	
	public BlockState getBlockAt(int x, int y, int z) {
		return Blocks.getBlockState(this.data[getBlockIndex(x, y, z)]);
	}
	
	public void setBlockAt(int x, int y, int z, BlockState state) {
		this.data[getBlockIndex(x, y, z)] = state.getSaveUid();
	}
	
	public boolean isBlockAt(int x, int y, int z, BlockState state) {
		return this.data[getBlockIndex(x, y, z)] == state.getSaveUid();
	}
	
	public BlockState setBlockAtPrevious(int x, int y, int z, BlockState state) {
		int idx = getBlockIndex(x, y, z);
		BlockState last = Blocks.getBlockState(this.data[idx]);
		this.data[idx] = state.getSaveUid();
		return last;
	}
	
	public BlockState getBlockAtIndex(int idx) {
		return Blocks.getBlockState(this.data[idx]);
	}
	
	/**
	 * Get block at a position relative to this chunk origin, but if coordinates are less
	 * than 0 or greater than 15, block state is retreived from (far) neighbours relatively
	 * to this chunk origin.
	 * @param x The relative X coordinate.
	 * @param y The relative Y coordinate.
	 * @param z The relative Z coordinate.
	 * @return The block state if found, else NULL.
	 */
	public BlockState getBlockAtBlockRel(int x, int y, int z) {
		
		int cy = this.position.getY() + (y >> 4);
		
		if (x < 0 || x > 15 || z < 0 || z > 15) {
			int cx = this.position.getX() + (x >> 4);
			int cz = this.position.getZ() + (z >> 4);
			WorldChunk chunk = this.world.getChunkAt(cx, cy, cz);
			return chunk == null ? null : chunk.getBlockAt(x & 15, y & 15, z & 15);
		} else if (cy != this.position.getY()) {
			WorldChunk chunk = this.section.getChunkAt(cy);
			return chunk == null ? null : chunk.getBlockAt(x, y & 15, z);
		} else {
			return this.getBlockAt(x, y, z);
		}
		
	}
	
	public BlockState getBlockAtBlockRel(int x, int y, int z, Direction direction) {
		
		switch (direction) {
			case TOP:
				if (y == 15) {
					WorldChunk c = this.section.getChunkAt(this.position.getY() + 1);
					return c == null ? null : c.getBlockAt(x, 0, z);
				}
				break;
			case BOTTOM:
				if (y == 0) {
					WorldChunk c = this.section.getChunkAt(this.position.getY() - 1);
					return c == null ? null : c.getBlockAt(x, 15, z);
				}
				break;
			case NORTH:
				if (x == 15) {
					WorldChunk c = this.world.getChunkAt(this.position.getX() + 1, this.position.getY(), this.position.getZ());
					return c == null ? null : c.getBlockAt(0, y, z);
				}
				break;
			case SOUTH:
				if (x == 0) {
					WorldChunk c = this.world.getChunkAt(this.position.getX() - 1, this.position.getY(), this.position.getZ());
					return c == null ? null : c.getBlockAt(15, y, z);
				}
				break;
			case EAST:
				if (z == 15) {
					WorldChunk c = this.world.getChunkAt(this.position.getX(), this.position.getY(), this.position.getZ() + 1);
					return c == null ? null : c.getBlockAt(x, y, 0);
				}
				break;
			case WEST:
				if (z == 0) {
					WorldChunk c = this.world.getChunkAt(this.position.getX(), this.position.getY(), this.position.getZ() - 1);
					return c == null ? null : c.getBlockAt(x, y, 15);
				}
				break;
		}
		
		return this.getBlockAt(x + direction.rx, y + direction.ry, z + direction.rz);
		
	}
	
	// ENTITIES //
	
	public void addEntity(Entity entity) {
	
		int ex = entity.getCurrentChunkPosX();
		int ey = entity.getCurrentChunkPosY();
		int ez = entity.getCurrentChunkPosZ();
		
		if (ex != this.position.getX() || ey != this.position.getY() || ez != this.position.getZ())
			throw new IllegalStateException("Can't add this entity in this chunk, because it's placed in chunk at " + ex + "/" + ey + "/" + ez + " and this chunk is at " + this.position);
		
		entity.setInChunk(true);
		entity.setChunkPos(ex, ey, ez);
		this.entities.add(entity);
		
	}
	
	public void removeEntity(Entity entity) {
		this.entities.remove(entity);
	}
	
	public void forEachEntitiesInBoundingBox(AxisAlignedBB boundingBox, Consumer<Entity> entityConsumer, boolean centerPointOnly) {
	
		for (Entity entity : this.entities) {
			if ((centerPointOnly && boundingBox.intersects(entity.getPosX(), entity.getPosY(), entity.getPosZ())) || (!centerPointOnly && boundingBox.intersects(entity.getBoundingBox()))) {
				entityConsumer.accept(entity);
			}
		}
	
	}
	
	// SAVING //

	/**
	 * Unsafe method to access internal block data.
	 * @return The internal block data array, of a length of 4096.
	 */
	public short[] getBlockData() {
		return this.data;
	}
	
	// UTILS //
	
	@Override
	public String toString() {
		return "WorldChunk{" +
				"position=" + position +
				'}';
	}
	
	public static int getBlockIndex(int x, int y, int z) {
		return (x << 8) | (y << 4) | z;
		// return x * 256 + y * 16 + z;
	}
	
	public static int getBlockX(int index) {
		return (index >> 8) & 15;
	}
	
	public static int getBlockY(int index) {
		return (index >> 4) & 15;
	}
	
	public static int getBlockZ(int index) {
		return index & 15;
	}
	
}
