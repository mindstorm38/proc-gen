package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.chunk.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.position.*;
import fr.theorozier.procgen.common.world.tick.WorldTickEntry;
import fr.theorozier.procgen.common.world.tick.WorldTickList;
import io.sutil.pool.FixedObjectPool;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorldServer extends WorldBase {
	
	public static final int NEAR_CHUNK_LOADING = 4;
	
	private final WorldDimensionManager dimensionManager;
	
	private final long seed;
	private final Random random;
	private final ChunkGeneratorProvider chunkGeneratorProvider;
	private final ChunkGenerator chunkGenerator;
	
	private final WorldTickList<Block> blockTickList;
	private final int seaLevel;
	
	private final Map<SectionPositioned, WorldPrimitiveSection> primitiveSections;
	private final Map<SectionPositioned, Future<WorldPrimitiveSection>> loadingSections;
	private final List<Future<WorldPrimitiveSection>> loadingSectionsFutures;
	private final HashSet<SectionPositioned> chunkLoadingPositions;
	
	public WorldServer(WorldDimensionManager dimensionManager, long seed, ChunkGeneratorProvider provider) {
		
		this.dimensionManager = dimensionManager;
		
		this.seed = seed;
		this.random = new Random(seed);
		this.chunkGeneratorProvider = provider;
		this.chunkGenerator = provider.create(this);
		
		this.blockTickList = new WorldTickList<>(this, Block::isTickable, this::tickBlock);
		this.seaLevel = 63;
		
		this.primitiveSections = new HashMap<>();
		this.loadingSections = new HashMap<>();
		this.loadingSectionsFutures = new ArrayList<>();
		this.chunkLoadingPositions = new HashSet<>();
		
	}
	
	// PROPERTIES //
	
	public WorldDimensionManager getDimensionManager() {
		return this.dimensionManager;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public Random getRandom() {
		return this.random;
	}
	
	public ChunkGeneratorProvider getChunkGeneratorProvider() {
		return this.chunkGeneratorProvider;
	}
	
	public ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}
	
	/**
	 * @return This world sea level.
	 */
	public int getSeaLevel() {
		return this.seaLevel;
	}
	
	// TICKING //
	
	@Override
	public void update() {
		
		super.update();
		
		this.updateChunkLoadingPositions();
		this.updateChunkLoading();
		
		this.blockTickList.tick();
		
		this.entities.forEach(Entity::update);
		
	}
	
	/**
	 * Check if a tick can be triggered at a position.
	 * @param pos The position to check.
	 * @return True if you can tick at this position.
	 */
	public boolean canTickAt(BlockPositioned pos) {
		return this.getChunkAtBlock(pos) != null;
	}
	
	public WorldTickList<Block> getBlockTickList() {
		return this.blockTickList;
	}
	
	/**
	 * Internal method to tick a block.
	 * @param entry The tick entry from tick list.
	 */
	private void tickBlock(WorldTickEntry<Block> entry) {
		
		BlockState block = this.getBlockAt(entry.getPosition());
		
		if (block.getBlock() == entry.getTarget()) {
			entry.getTarget().tickBlock(this, entry.getPosition(), block, this.random);
		}
		
	}
	
	// SECTIONS //
	
	protected WorldServerSection getSectionAt(int x, int z) {
		return (WorldServerSection) super.getSectionAt(x, z);
	}
	
	protected WorldServerSection getSectionAtBlock(int x, int z) {
		return (WorldServerSection) super.getSectionAtBlock(x, z);
	}
	
	// CHUNKS //
	
	public WorldServerChunk getChunkAt(int x, int y, int z) {
		return (WorldServerChunk) super.getChunkAt(x, y, z);
	}
	
	public WorldServerChunk getChunkAtBlock(int x, int y, int z) {
		return (WorldServerChunk) super.getChunkAtBlock(x, y, z);
	}
	
	public WorldServerChunk getChunkAtBlock(BlockPositioned pos) {
		return (WorldServerChunk) super.getChunkAtBlock(pos);
	}
	
	// HEIGHTMAPS //
	
	public short getHeightAt(Heightmap.Type type, int x, int z) {
		WorldServerSection section = this.getSectionAtBlock(x, z);
		return section == null ? 0 : section.getHeightAt(type, x & 15, z & 15);
	}
	
	public short getHeightAt(Heightmap.Type type, SectionPositioned pos) {
		return this.getHeightAt(type, pos.getX(), pos.getZ());
	}
	
	public ImmutableBlockPosition getBlockHeightAt(Heightmap.Type type, SectionPositioned pos) {
		return new ImmutableBlockPosition(pos, this.getHeightAt(type, pos));
	}
	
	// CHUNK LOADING //
	
	public void addChunkLoadingPosition(SectionPositioned sectionPositioned) {
		this.chunkLoadingPositions.add(sectionPositioned);
	}
	
	public void removeChunkLoadingPosition(SectionPositioned sectionPositioned) {
		this.chunkLoadingPositions.remove(sectionPositioned);
	}
	
	private void updateChunkLoadingPositions() {
		
		for (SectionPositioned poses : this.chunkLoadingPositions) {
			this.forEachSectionPosNear(poses.getX(), poses.getZ(), NEAR_CHUNK_LOADING, this::tryLoadSection);
		}
		
	}
	
	private void tryLoadSection(SectionPosition sectionPosition) {
		
		int x = sectionPosition.getX();
		int z = sectionPosition.getZ();
		
		if (!this.isSectionLoadedAtBlock(x, z) && !this.isSectionLoadingAt(x, z)) {
			
			System.out.println("Section " + sectionPosition + " is not loaded, creating new one ...");
			
			WorldPrimitiveSection newPrimitive = new WorldPrimitiveSection(this, sectionPosition);
			this.primitiveSections.put(sectionPosition, newPrimitive);
			this.submitSectionNextStatusLoadingTask(sectionPosition, newPrimitive);
			
		}
	
	}
	
	private void updateChunkLoading() {
	
		Iterator<Future<WorldPrimitiveSection>> loadingSectionFuturesIt = this.loadingSectionsFutures.iterator();
		Future<WorldPrimitiveSection> future;
		WorldPrimitiveSection section = null;
		
		ArrayList<WorldPrimitiveSection> doneSections = new ArrayList<>();
		
		while (loadingSectionFuturesIt.hasNext()) {
			
			future = loadingSectionFuturesIt.next();
			
			if (future.isDone()) {
				
				try {
					
					section = future.get();
					section.gotoNextStatus();
					
					System.out.println("==> Section at " + section.getSectionPos() + " loaded status " + section.getStatus().getIdentifier());
					
					doneSections.add(section);
					
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				
				loadingSectionFuturesIt.remove();
				
				if (section != null) {
					this.loadingSections.remove(section.getSectionPos());
				}
				
			}
			
		}
		
		for (WorldPrimitiveSection doneSection : doneSections) {
			this.submitSectionNextStatusLoadingTask(doneSection.getSectionPos(), doneSection);
		}
	
	}
	
	private void submitSectionNextStatusLoadingTask(SectionPositioned pos, WorldPrimitiveSection section) {
		
		PriorityRunnable task = section.getNextStatusLoadingTask(this, 0);
		
		if (task != null) {
			
			System.out.println("Section at " + pos + " loading status " + section.getStatus().getNext().getIdentifier() + "...");
			
			Future<WorldPrimitiveSection> taskFuture = this.dimensionManager.submitWorldLoadingTask(section, task);
			this.loadingSections.put(pos, taskFuture);
			this.loadingSectionsFutures.add(taskFuture);
			
		}
		
	}
	
	public WorldPrimitiveSection getPrimitiveSectionAt(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.primitiveSections.get(pos.get().set(x, z));
		}
	}
	
	public boolean isSectionLoadingAt(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.primitiveSections.containsKey(pos.get().set(x, z));
		}
	}
	
	// FIXME : TEMPORARY FOR MONOTHREAD GENERATION
	public void loadSection(SectionPosition pos) {
		
		WorldServerSection section = this.getSectionAt(pos.getX(), pos.getZ());
		
		if (section == null) {
			
			ImmutableSectionPosition immutable = new ImmutableSectionPosition(pos);
			
			section = new WorldServerSection(this, immutable);
			this.sections.put(immutable, section);
			
			section.generate();
			
		}
		
	}
	
	public void loadNear(float x, float z) {
		this.forEachSectionPosNear(x, z, NEAR_CHUNK_LOADING, this::loadSection);
	}
	
	// FIXME : TEMPORARY FOR ENTITY TESTING
	public void rawAddEntity(Entity entity) {
		super.addEntity(entity);
	}
	
}
