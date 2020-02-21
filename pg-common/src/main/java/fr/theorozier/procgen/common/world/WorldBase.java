package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.event.WorldMethodEventManager;
import fr.theorozier.procgen.common.world.position.*;
import io.msengine.common.util.GameProfiler;
import io.msengine.common.util.event.MethodEventManager;
import io.sutil.math.MathHelper;
import io.sutil.pool.FixedObjectPool;
import io.sutil.profiler.Profiler;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * Base world class storing data needed for client and server.<br>
 * In reality this a world dimension, stored and managed by a {@link WorldServer}.
 *
 * @author Theo Rozier
 *
 */
public abstract class WorldBase implements WorldAccessor {
	
	protected static final Profiler PROFILER = GameProfiler.getInstance();
	
	protected final Map<AbsSectionPosition, WorldSection> sections = new HashMap<>();
	protected final Map<Long, Entity> entitiesById = new HashMap<>();
	protected final List<Entity> entities = new ArrayList<>();
	protected final List<Entity> entitiesView = Collections.unmodifiableList(this.entities);
	
	protected long time = 0L;
	
	protected final MethodEventManager eventManager;
	
	public WorldBase() {
		this.eventManager = new WorldMethodEventManager();
	}
	
	// PROPERTIES //

	/**
	 * @return True if this world is a server one, must be overriden in subclass if return true is needed.
	 */
	public boolean isServer() {
		return false;
	}

	/**
	 * @return This world base casted to world server.
	 * @throws UnsupportedOperationException If the world is not a server world.
	 */
	public WorldDimension getAsServer() {
		throw new UnsupportedOperationException("This world is not a server world.");
	}
	
	/**
	 * @return Get currnet game tick time.
	 */
	public long getTime() {
		return this.time;
	}

	@Override
	public short getVerticalChunkCount() {
		return 16;
	}
	
	public MethodEventManager getEventManager() {
		return this.eventManager;
	}
	
	// UPDATE //
	
	/**
	 * Run a single tick in this world.
	 */
	public void update() {
		this.updateTime();
	}
	
	/**
	 * Internal method to increase overall world time each tick.
	 */
	protected void updateTime() {
		++this.time;
	}
	
	// SECTIONS //
	
	@Override
	public WorldSection getSectionAt(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.sections.get(pos.get().set(x, z));
		}
	}
	
	@Override
	public WorldSection getSectionAt(AbsSectionPosition pos) {
		return this.sections.get(pos);
	}
	
	@Override
	public boolean isSectionLoadedAt(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.sections.containsKey(pos.get().set(x, z));
		}
	}
	
	@Override
	public boolean isSectionLoadedAt(AbsSectionPosition pos) {
		return this.sections.containsKey(pos);
	}
	
	public void forEachSection(Consumer<WorldSection> cons) {
		this.sections.values().forEach(cons);
	}
	
	// CHUNKS //
	
	@Override
	public WorldChunk getChunkAt(int x, int y, int z) {
		WorldSection section = this.getSectionAt(x, z);
		return section == null ? null : section.getChunkAt(y);
	}
	
	// BIOMES //
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		WorldSection section = this.getSectionAtBlock(x, z);
		return section == null ? null : section.getBiomeAtBlock(x, z);
	}
	
	// BLOCKS //
	
	@Override
	public BlockState getBlockAt(int x, int y, int z) {
		WorldChunk chunk = this.getChunkAtBlock(x, y, z);
		return chunk == null ? null : chunk.getBlockAt(x & 15, y & 15, z & 15);
	}
	
	@Override
	public void setBlockAt(int x, int y, int z, BlockState state) {
		WorldChunk chunk = this.getChunkAtBlock(x, y, z);
		if (chunk != null) {
			chunk.setBlockAt(x & 15, y & 15, z & 15, state);
			try (FixedObjectPool<BlockPosition>.PoolObject pos = BlockPosition.POOL.acquire()) {
				this.eventManager.fireListeners(WorldChunkListener.class, l -> l.worldChunkBlockChanged(this, chunk, pos.get().set(x, y, z), state));
			}
		}
	}
	
	@Override
	public boolean isBlockAt(int x, int y, int z, BlockState state) {
		WorldChunk chunk = this.getChunkAtBlock(x, y, z);
		return chunk != null && chunk.isBlockAt(x & 15, y & 15, z & 15, state);
	}
	
	public void forEachBlocksIn(AxisAlignedBB boundingBox, BiConsumer<BlockState, BlockPositioned> blockConsumer) {
		
		int xMin = MathHelper.floorDoubleInt(boundingBox.getMinX()) - 1;
		int yMin = MathHelper.floorDoubleInt(boundingBox.getMinY()) - 1;
		int zMin = MathHelper.floorDoubleInt(boundingBox.getMinZ()) - 1;
		
		int xMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxX()) + 1;
		int yMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxY()) + 1;
		int zMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxZ()) + 1;
		
		BlockState state;
		
		try (FixedObjectPool<BlockPosition>.PoolObject pos = BlockPosition.POOL.acquire()) {
			
			for (int y = yMin; y <= yMax; ++y)
				for (int x = xMin; x <= xMax; ++x)
					for (int z = zMin; z <= zMax; ++z)
						if ((state = this.getBlockAt(x, y, z)) != null && state.isBoundingBoxColliding(x, y, z, boundingBox))
							blockConsumer.accept(state, pos.get().set(x, y, z));
			
		}
		
	}
	
	public void forEachBoundingBoxesIn(AxisAlignedBB boundingBox, Consumer<AxisAlignedBB> bbConsumer) {
		
		int xMin = MathHelper.floorDoubleInt(boundingBox.getMinX()) - 1;
		int yMin = MathHelper.floorDoubleInt(boundingBox.getMinY()) - 1;
		int zMin = MathHelper.floorDoubleInt(boundingBox.getMinZ()) - 1;
		
		int xMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxX()) + 1;
		int yMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxY()) + 1;
		int zMax = MathHelper.ceilingDoubleInt(boundingBox.getMaxZ()) + 1;
		
		BlockState state;
		
		for (int y = yMin; y <= yMax; ++y)
			for (int x = xMin; x <= xMax; ++x)
				for (int z = zMin; z <= zMax; ++z)
					if ((state = this.getBlockAt(x, y, z)) != null)
						state.forEachCollidingBoundingBox(x, y, z, boundingBox, bbConsumer);
		
	}
	
	// ENTITIES //
	
	public void forEachEntitiesIn(AxisAlignedBB boundingBox, Consumer<Entity> entityConsumer, boolean centerPointOnly) {
	
		int minCx = MathHelper.floorDoubleInt(boundingBox.getMinX()) >> 4;
		int minCy = MathHelper.floorDoubleInt(boundingBox.getMinY()) >> 4;
		int minCz = MathHelper.floorDoubleInt(boundingBox.getMinZ()) >> 4;
	
		int maxCx = MathHelper.floorDoubleInt(boundingBox.getMaxX()) >> 4;
		int maxCy = MathHelper.floorDoubleInt(boundingBox.getMaxY()) >> 4;
		int maxCz = MathHelper.floorDoubleInt(boundingBox.getMaxZ()) >> 4;

		WorldChunk chunk;
		
		for (int cx = minCx; cx <= maxCx; ++cx) {
			for (int cy = minCy; cy <= maxCy; ++cy) {
				for (int cz = minCz; cz <= maxCz; ++cz) {
					if ((chunk = this.getChunkAt(cx, cy, cz)) != null) {
						chunk.forEachEntitiesInBoundingBox(boundingBox, entityConsumer, centerPointOnly);
					}
				}
			}
		}
		
	}
	
	public final List<Entity> getEntitiesView() {
		return this.entitiesView;
	}
	
	// UTILITES //
	
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
		
		try (FixedObjectPool<SectionPosition>.PoolObject obj = SectionPosition.POOL.acquire()) {
			
			for (int xv = minPos.getX(); xv <= xmax; ++xv)
				for (int zv = minPos.getZ(); zv <= zmax; ++zv)
					consumer.accept(obj.get().set(xv, zv));
			
		}
		
	}
	
}
