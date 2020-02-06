package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldSectionBlockRegistry;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.chunk.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.gen.chunk.WorldSectionStatus;
import fr.theorozier.procgen.common.world.position.*;
import fr.theorozier.procgen.common.world.tick.WorldTickEntry;
import fr.theorozier.procgen.common.world.tick.WorldTickList;
import io.sutil.buffer.VariableBuffer;
import io.sutil.pool.FixedObjectPool;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

public class WorldServer extends WorldBase {
	
	public static final int NEAR_CHUNK_LOADING = 4;
	
	private final WorldDimensionManager dimensionManager;
	private final File worldDir;
	private final File sectionsDir;
	private final File sectionsBackupDir;
	
	private final long seed;
	private final Random random;
	private final ChunkGeneratorProvider chunkGeneratorProvider;
	private final ChunkGenerator chunkGenerator;
	
	private final WorldTickList<Block> blockTickList;
	private final int seaLevel;
	
	// Keep using SectionPositioned to allow queries using mutable SectionPosition, but rememeber to only put immutable ones as keys.
	private final Map<SectionPositioned, WorldPrimitiveSection> primitiveSections = new HashMap<>();
	private final Map<SectionPositioned, Future<WorldPrimitiveSection>> loadingSections = new HashMap<>();
	private final List<ImmutableSectionPosition> primitiveSectionsList = new ArrayList<>();
	private final Map<SectionPositioned, Boolean> savedSections = new HashMap<>();
	
	private final HashSet<WorldLoadingPosition> worldLoadingPositions = new HashSet<>();
	
	public WorldServer(WorldDimensionManager dimensionManager, File worldDir, long seed, ChunkGeneratorProvider provider) {
		
		this.dimensionManager = Objects.requireNonNull(dimensionManager);
		this.worldDir = Objects.requireNonNull(worldDir);
		this.sectionsDir = new File(worldDir, "sections");
		this.sectionsBackupDir = new File(worldDir, "sections_bk");
		
		SaveUtils.mkdirOrThrowException(this.sectionsDir, "Chunks directory already exists but it's not a directory.");
		SaveUtils.mkdirOrThrowException(this.sectionsBackupDir, "Chunks backup directory already exists but it's not a directory.");
		
		this.seed = seed;
		this.random = new Random(seed);
		this.chunkGeneratorProvider = Objects.requireNonNull(provider);
		this.chunkGenerator = provider.create(this);
		
		this.blockTickList = new WorldTickList<>(this, Block::isTickable, this::tickBlock);
		this.seaLevel = 63;
		
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
	
	@Override
	public boolean isServer() {
		return true;
	}
	
	@Override
	public WorldServer getAsServer() {
		return this;
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
		
		ListIterator<Entity> entitiesIt = this.entities.listIterator();
		
		while (entitiesIt.hasNext()) {
			entitiesIt.next().update();
		}
		
		while (entitiesIt.hasPrevious()) {
			
			Entity entity = entitiesIt.previous();
			
			if (entity.isDead()) {
				
				entitiesIt.remove();
				this.removeEntity(entity, false);
				
			}
			
		}
		
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
		if (this.isSectionLoadingAt(x, z)) {
			return this.getPrimitiveSectionAt(x, z);
		} else {
			return (WorldServerSection) super.getSectionAt(x, z);
		}
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
	
	public void addWorldLoadingPosition(WorldLoadingPosition sectionPositioned) {
		this.worldLoadingPositions.add(sectionPositioned);
	}
	
	public void removeWorldLoadingPosition(WorldLoadingPosition sectionPositioned) {
		this.worldLoadingPositions.remove(sectionPositioned);
	}
	
	private void updateChunkLoadingPositions() {
		
		for (WorldLoadingPosition poses : this.worldLoadingPositions) {
			this.forEachSectionPosNear(poses.getX(), poses.getZ(), poses.getLoadingRadius(), this::tryLoadSection);
		}
		
	}
	
	private int getDistanceToLoaders(SectionPositioned sectionPos) {
		
		return this.worldLoadingPositions.stream()
				.mapToInt(sp -> (int) sp.distSquared(sectionPos))
				.min()
				.orElse(0);
		
	}
	
	private void tryLoadSection(SectionPosition sectionPosition) {
		
		int x = sectionPosition.getX();
		int z = sectionPosition.getZ();
		
		if (!this.isSectionLoadedAt(x, z) && !this.isSectionLoadingAt(x, z) && !this.isSectionSaved(sectionPosition)) {
			
			ImmutableSectionPosition immutableSectionPosition = sectionPosition.immutable();
			WorldPrimitiveSection primitive = new WorldPrimitiveSection(this, immutableSectionPosition);
			
			this.primitiveSections.put(immutableSectionPosition, primitive);
			this.primitiveSectionsList.add(immutableSectionPosition);
			
			this.submitSectionNextStatusLoadingTask(immutableSectionPosition, primitive, this.getDistanceToLoaders(sectionPosition));
			
		}
	
	}
	
	private void updateChunkLoading() {
		
		Iterator<ImmutableSectionPosition> primitiveSectionsIt = this.primitiveSectionsList.iterator();
		ImmutableSectionPosition pos;
		Future<WorldPrimitiveSection> future;
		WorldPrimitiveSection section;
		
		while (primitiveSectionsIt.hasNext()) {
			
			pos = primitiveSectionsIt.next();
			future = this.loadingSections.get(pos);
			
			if (future == null ) {
			
				section = this.primitiveSections.get(pos);
				this.submitSectionNextStatusLoadingTask(pos, section, this.getDistanceToLoaders(pos));
			
			} else {
			
				if (future.isDone()) {
					
					try {
						
						section = future.get();
						section.gotoNextStatus();
						
						if (section.isFinished()) {
							
							WorldServerSection newSection = new WorldServerSection(section);
							this.sections.put(pos, newSection);
							
							this.primitiveSections.remove(pos);
							primitiveSectionsIt.remove();
							
							newSection.forEachChunk(chunk ->
								this.eventManager.fireListeners(WorldLoadingListener.class, l ->
									l.worldChunkLoaded(this, chunk)
								)
							);
							
							/*
							this.dimensionManager.submitOtherTask(new PriorityRunnable() {
								
								public int getPriority() { return 0; }
								
								public void run() {
									WorldServer.this.saveSection(newSection);
								}
								
							});
							*/
							
						}
						
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} finally {
						this.loadingSections.remove(pos);
					}
					
				}
				
			}
			
		}
		
	}
	
	private void submitSectionNextStatusLoadingTask(ImmutableSectionPosition pos, WorldPrimitiveSection section, int distanceToLoaders) {
		
		PriorityRunnable task = section.getNextStatusLoadingTask(this, distanceToLoaders);
		
		if (task != null) {
			
			Future<WorldPrimitiveSection> taskFuture = this.dimensionManager.submitWorldLoadingTask(section, task);
			this.loadingSections.put(pos, taskFuture);
			
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
	
	public void debugLoadingChunkAround(int x, int z, int range) {
		
		int minX = x - range;
		int minZ = z - range;
		
		int maxX = x + range;
		int maxZ = z + range;
		
		WorldPrimitiveSection section;
		WorldSectionStatus status;
		
		System.out.print("   ");
		for (x = minX; x <= maxX; ++x) {
			if (x >= 0) System.out.print(' ');
			System.out.print(x);
		}
		System.out.println();
		
		for (z = minZ; z <= maxZ; ++z) {
			
			if (z >= 0) System.out.print(' ');
			System.out.print(z);
			System.out.print(" ");
			
			for (x = minX; x <= maxX; ++x) {
				
				section = this.getPrimitiveSectionAt(x, z);
				
				if (section == null) {
					System.out.print("  ");
				} else {
					
					status = section.getStatus();
					
					System.out.print(' ');
					
					if (status == WorldSectionStatus.EMPTY) {
						System.out.print("\u001B[34mE\u001B[0m");
					} else if (status == WorldSectionStatus.BIOMES) {
						System.out.print("\u001B[35mB\u001B[0m");
					} else if (status == WorldSectionStatus.BASE) {
						System.out.print("\u001B[31mT\u001B[0m");
					} else if (status == WorldSectionStatus.SURFACE) {
						System.out.print("\u001B[33mS\u001B[0m");
					} else if (status == WorldSectionStatus.FEATURES) {
						System.out.print("\u001B[32mF\u001B[0m");
					} else if (status == WorldSectionStatus.FINISHED) {
						System.out.print("O");
					} else {
						System.out.print("?");
					}
					
				}
				
			}
			
			System.out.println();
			
		}
		
	}
	
	// SECTIONS SAVING //
	
	public boolean isSectionSaved(SectionPositioned pos) {
		
		return this.savedSections.computeIfAbsent(pos, p ->
				new File(this.sectionsDir, getSectionFileName(p.immutableSectionPos())).isFile());
		
	}
	
	public void saveSection(WorldServerSection section) {
		
		File sectionFile = new File(this.sectionsDir, getSectionFileName(section.getSectionPos()));
		
		if (sectionFile.isDirectory())
			throw new IllegalStateException("The section at " + section.getSectionPos() + " can't be saved because a directory already exists with the name '" + sectionFile.getPath() + "'.");
		
		try {
			
			OutputStream stream = new GZIPOutputStream(new FileOutputStream(sectionFile));
			
			WorldSectionBlockRegistry blockRegistry = new WorldSectionBlockRegistry();
			
			VariableBuffer headerBuf = new VariableBuffer(ByteOrder.BIG_ENDIAN, 256);
			VariableBuffer sectionBuf = new VariableBuffer(ByteOrder.BIG_ENDIAN);
			VariableBuffer chunkBuf = new VariableBuffer(ByteOrder.BIG_ENDIAN, 8192);
			
			int chunkCount = this.getVerticalChunkCount();
			int chunkSize;
			
			for (int y = 0; y < chunkCount; ++y) {
				
				chunkBuf.setWriteIndex(0);
				section.getChunkAt(y).saveChunk(blockRegistry, chunkBuf);
				chunkSize = chunkBuf.getWriteIndex();
				
				sectionBuf.writeInteger(chunkSize);
				
				if (chunkSize != 0)
					sectionBuf.writeBuffer(chunkBuf, chunkSize);
				
			}
			
			
			
			blockRegistry.foreachStates((state, uid) -> {
				
				headerBuf.writeShort(uid);
				headerBuf.writeStringIndexed(state.getBlock().getIdentifier());
				headerBuf.writeUnsignedByte((short) state.getPropertiesCount()); // Using byte because state with so much properties can not be stored.
				
				state.getProperties().forEach((property, value) -> {
					headerBuf.writeStringIndexed(property.getName());
					headerBuf.writeStringIndexed(property.getValueNameSafe(value)); // Should never throw cast exceptions
				});
				
			});
			
			stream.write(headerBuf.getBytes(), 0, headerBuf.getWriteIndex());
			stream.write(sectionBuf.getBytes(), 0, sectionBuf.getWriteIndex());
			
			stream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getSectionFileName(SectionPositioned pos) {
		return pos.getX() + "." + pos.getZ() + ".pgs";
	}
	
	// FIXME : TEMPORARY FOR ENTITY TESTING
	public void rawAddEntity(Entity entity) {
		super.addEntity(entity);
	}
	
}
