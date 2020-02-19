package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.task.section.WorldSectionBlockRegistry;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.buffer.VariableBuffer;

import java.util.Arrays;
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
		return x * 256 + y * 16 + z;
	}
	
}
