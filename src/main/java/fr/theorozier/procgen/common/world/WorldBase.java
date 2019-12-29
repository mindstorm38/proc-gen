package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.position.*;
import io.msengine.common.util.event.MethodEventManager;
import io.sutil.math.MathHelper;
import io.sutil.pool.FixedObjectPool;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * Base world class storing data needed for client and server.
 *
 * @author Theo Rozier
 *
 */
public abstract class WorldBase {

	protected final Map<SectionPositioned, WorldSection> sections = new HashMap<>();
	protected long time;
	
	protected final MethodEventManager eventManager;
	
	public WorldBase() {
		
		this.time = 0L;
		
		this.eventManager = new MethodEventManager(
				WorldChunkListener.class,
				WorldLoadingListener.class
		);
		
	}
	
	/**
	 * Run a single tick in this world.
	 */
	public void update() {
		
		++this.time;
		
	}
	
	/**
	 * @return Get currnet game tick time.
	 */
	public long getTime() {
		return this.time;
	}
	
	/**
	 * @return Number of chunks in a section's height.
	 */
	public int getVerticalChunkCount() {
		return 16;
	}
	
	/**
	 * @return The height limit of the world.
	 */
	public int getHeightLimit() {
		return this.getVerticalChunkCount() * 16;
	}
	
	public MethodEventManager getEventManager() {
		return this.eventManager;
	}
	
	// SECTIONS //
	
	/**
	 * Internal method to get a section at specific position.
	 * @param x The X section coordinate.
	 * @param z The Z section coordinate.
	 * @return Section at this position, or <b>NULL</b> if no section there.
	 */
	protected WorldSection getSectionAt(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.sections.get(pos.get().set(x, z));
		}
	}
	
	protected WorldSection getSectionAtBlock(int x, int z) {
		return this.getSectionAt(x >> 4, z >> 4);
	}
	
	// CHUNKS //
	
	/**
	 * Method to get a chunk at specific position.
	 * @param x The X chunk coordinate.
	 * @param y The Y chunk coordinate.
	 * @param z The Z chunk coordinate.
	 * @return Chunk at this position, or <b>NULL</b> if no chunk there.
	 */
	public WorldChunk getChunkAt(int x, int y, int z) {
		WorldSection section = this.getSectionAt(x, z);
		return section == null ? null : section.getChunkAt(y);
	}
	
	public WorldChunk getChunkAtBlock(int x, int y, int z) {
		return this.getChunkAt(x >> 4, y >> 4, z >> 4);
	}
	
	public WorldChunk getChunkAtBlock(BlockPositioned pos) {
		return this.getChunkAtBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	// BIOMES //
	
	public Biome getBiomeAt(int x, int z) {
		WorldSection section = this.getSectionAtBlock(x, z);
		return section == null ? null : section.getBiomeAtBlock(x, z);
	}
	
	public Biome getBiomeAt(SectionPositioned pos) {
		return this.getBiomeAt(pos.getX(), pos.getZ());
	}
	
	// BLOCKS //
	
	public BlockState getBlockAt(int x, int y, int z) {
		WorldChunk chunk = this.getChunkAtBlock(x, y, z);
		return chunk == null ? null : chunk.getBlockAt(x & 15, y & 15, z & 15);
	}
	
	public BlockState getBlockAt(BlockPositioned pos) {
		return this.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public void setBlockAt(int x, int y, int z, BlockState state) {
		WorldChunk chunk = this.getChunkAtBlock(x, y, z);
		if (chunk != null) chunk.setBlockAt(x & 15, y & 15, z & 15, state);
	}
	
	public void setBlockAt(BlockPositioned pos, BlockState state) {
		this.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), state);
	}
	
	// UTILITES //
	
	public void forEachBoundingBoxesIn(AxisAlignedBB boundingBox, Consumer<AxisAlignedBB> bbConsumer) {
	
		int xMin = MathHelper.floorFloatInt(boundingBox.getMinX()) - 1;
		int xMax = MathHelper.ceilingFloatInt(boundingBox.getMaxX()) + 1;
		
		int yMin = MathHelper.floorFloatInt(boundingBox.getMinY()) - 1;
		int yMax = MathHelper.ceilingFloatInt(boundingBox.getMaxY()) + 1;
		
		int zMin = MathHelper.floorFloatInt(boundingBox.getMinZ()) - 1;
		int zMax = MathHelper.ceilingFloatInt(boundingBox.getMaxZ()) + 1;
		
		BlockState state;
		
		for (; yMin <= yMax; ++yMin)
			for (; xMin <= xMax; ++xMin)
				for (; zMin <= zMax; ++zMin)
					if ((state = this.getBlockAt(xMin, yMin, zMin)) != null)
						state.forEachBoundingBox(bbConsumer);
		
	}
	
	public void forEachChunkPosNear(float x, float y, float z, int range, boolean wholeY, Consumer<BlockPosition> consumer) {
		
		ImmutableBlockPosition chunkPos = new ImmutableBlockPosition(MathHelper.floorFloatInt(x) >> 4, MathHelper.floorFloatInt(y) >> 4, MathHelper.floorFloatInt(z) >> 4);
		BlockPosition minPos = new BlockPosition(chunkPos).sub(range, range, range);
		
		int xmax = chunkPos.getX() + range;
		int ymax = wholeY ? this.getVerticalChunkCount() : chunkPos.getY() + range;
		int zmax = chunkPos.getZ() + range;
		
		BlockPosition temp = new BlockPosition();
		
		for (int xv = minPos.getX(); xv <= xmax; ++xv)
			for (int yv = (wholeY ? 0 : minPos.getY()); yv <= ymax; ++yv)
				for (int zv = minPos.getZ(); zv <= zmax; ++zv)
					consumer.accept(temp.set(xv, yv, zv));
		
	}
	
	public void forEachChunkNear(float x, float y, float z, int range, Consumer<WorldChunk> consumer) {
		
		this.forEachChunkPosNear(x, y, z, range, false, pos -> {
			WorldChunk ck = this.getChunkAt(pos.getX(), pos.getY(), pos.getZ());
			if (ck != null) consumer.accept(ck);
		});
		
	}
	
	public void forEachSectionPosNear(float x, float z, int range, Consumer<SectionPosition> consumer) {
		
		ImmutableSectionPosition sectionPos = new ImmutableSectionPosition(MathHelper.floorFloatInt(x) >> 4, MathHelper.floorFloatInt(z) >> 4);
		SectionPosition minPos = new SectionPosition(sectionPos).sub(range, range);
		
		int xmax = sectionPos.getX() + range;
		int zmax = sectionPos.getZ() + range;
		
		SectionPosition temp = new SectionPosition();
		
		for (int xv = minPos.getX(); xv <= xmax; ++xv)
			for (int zv = minPos.getZ(); zv <= zmax; ++zv)
				consumer.accept(temp.set(xv, zv));
		
	}
	
}
