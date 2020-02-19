package fr.theorozier.procgen.common.world.task;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.AbsSectionPosition;
import fr.theorozier.procgen.common.world.task.section.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import fr.theorozier.procgen.common.world.task.section.WorldSectionStatus;
import io.sutil.pool.FixedObjectPool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * A dimension loader, handle regions saving/loading.
 *
 * @author Théo Rozier
 *
 */
public class DimensionLoader {
	
	private final WorldDimension dimension;
	private final ChunkGenerator generator;
	
	private final File worldDir;
	private final File regionsDir;
	
	// File loading system.
	private final Map<AbsSectionPosition, DimensionRegionFile> regions = new HashMap<>();
	
	// Common loading system, using primitive sections.
	private final Map<AbsSectionPosition, WorldPrimitiveSection> primitiveSections = new HashMap<>();
	private final Map<AbsSectionPosition, Future<WorldTask>> tasks = new HashMap<>();

	// This list also contains all primitive sections positions that will be only deleted when primitive section is sent to dimension.
	private final List<ImmutableSectionPosition> tasksList = new ArrayList<>();
	private final Deque<ImmutableSectionPosition> saveQueue = new ArrayDeque<>();
	
	private final VirtualLoaderWorld virtualWorld;
	
	public DimensionLoader(WorldDimension dimension) {
		
		if (dimension.getLoader() != null)
			throw new IllegalArgumentException("This dimension already had a loader.");
		
		this.dimension = dimension;
		this.generator = Objects.requireNonNull(this.dimension.getMetadata().getChunkGeneratorProvider().create(this.dimension), "ChunkGenerator provider returned Null.");
		
		this.worldDir = dimension.getDirectory();
		this.regionsDir = new File(this.worldDir, "regions");
		
		SaveUtils.mkdirOrThrowException(this.regionsDir, "The sections already exists but it's a file.");
		
		this.virtualWorld = new VirtualLoaderWorld();
		
	}
	
	public WorldDimension getDimension() {
		return this.dimension;
	}
	
	public ChunkGenerator getGenerator() {
		return this.generator;
	}
	
	public File getWorldDir() {
		return this.worldDir;
	}
	
	public File getRegionsDir() {
		return this.regionsDir;
	}

	/**
	 * @return Internal virtual that delegate calls to non-primitive sections as expected, but
	 * 	       also allow interraction with primitive sections.
	 */
	public WorldAccessorServer getVirtualWorld() {
		return this.virtualWorld;
	}
	
	public void loadSection(AbsSectionPosition pos) {
		
		if (this.isSectionLoading(pos))
			return;
		
		ImmutableSectionPosition immutablePos = pos.immutableSectionPos();
		WorldPrimitiveSection primitiveSection = new WorldPrimitiveSection(this.dimension, immutablePos);
		
		this.primitiveSections.put(immutablePos, primitiveSection);
		this.tasksList.add(immutablePos);
		
		int distanceToLoaders = this.getDistanceToLoaders(pos);
		
		if (this.isSectionSaved(immutablePos)) {
			
			primitiveSection.setStatus(WorldSectionStatus.LOADING);
			WorldTask task = primitiveSection.getLoadingTask(this, distanceToLoaders);
			Future<WorldTask> future = this.dimension.getTaskManager().submitWorldTask(task);
			
			this.tasks.put(immutablePos, future);
			
		} else {
			this.submitNextStatusGenerateTask(immutablePos, primitiveSection, distanceToLoaders);
		}
		
	}
	
	public void saveSection(AbsSectionPosition pos) {
		
		if (this.isSectionLoading(pos))
			return;
		
		WorldServerSection section = this.dimension.getSectionAt(pos);

		if (section != null) {
			
			ImmutableSectionPosition immutablePos = section.getSectionPos();
			WorldTask task = section.getSavingTask(this);
			
			if (task != null) {
				
				Future<WorldTask> future = this.dimension.getTaskManager().submitWorldTask(task);
				
				this.tasks.put(immutablePos, future);
				this.tasksList.add(immutablePos);
				
			}
			
		}

	}
	
	public void saveSectionAfter(AbsSectionPosition pos) {
		this.saveQueue.addLast(pos.immutableSectionPos());
	}
	
	public void update() {
		
		ImmutableSectionPosition immutablePos;
		
		while ((immutablePos = this.saveQueue.pollFirst()) != null) {
			this.saveSection(immutablePos);
		}
		
		Future<WorldTask> futureTask;
		WorldTask doneTask;
		
		for (int i = 0; i < this.tasksList.size(); ++i) {
			
			immutablePos = this.tasksList.get(i);
			futureTask = this.tasks.get(immutablePos);
			
			if (futureTask == null) {
				
				this.submitNextStatusGenerateTask(immutablePos, this.primitiveSections.get(immutablePos), this.getDistanceToLoaders(immutablePos));
				
			} else {
				
				if (futureTask.isDone()) {
					
					try {
						doneTask = futureTask.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
						return;
					}
					
					if (doneTask.hasPrimitiveSection()) {
						if (doneTask.getPrimitiveSection().gotoNextStatus()) {
						
							this.dimension.loadPrimitiveSection(doneTask.getPrimitiveSection());
							this.primitiveSections.remove(immutablePos);
							this.tasksList.remove(i--);
							
						}
					} else {
						this.tasksList.remove(i--);
					}
					
					this.tasks.remove(immutablePos);
					
				}
				
			}
			
		}
		
	}
	
	private int getDistanceToLoaders(SectionPositioned pos) {
		return this.dimension.getDistanceToLoaders(pos);
	}
	
	private void submitNextStatusGenerateTask(ImmutableSectionPosition pos, WorldPrimitiveSection section, int distanceToLoaders) {
		
		WorldTask task = section.getNextStatusGenerateTask(this, distanceToLoaders);
		
		if (task != null) {
			
			Future<WorldTask> future = this.dimension.getTaskManager().submitWorldTask(task);
			this.tasks.put(pos, future);
			
		}
		
	}
	
	/**
	 * To know if a section is loading.
	 * @param pos The section position.
	 * @return True if the section is currently loading.
	 */
	public boolean isSectionLoading(AbsSectionPosition pos) {
		return this.primitiveSections.containsKey(pos) || this.tasks.containsKey(pos);
	}
	
	/**
	 * Get primitive section at specified position.
	 * @param pos Section position.
	 * @return The primitive section, or Null if no primitive section there.
	 */
	public WorldPrimitiveSection getPrimitiveSection(AbsSectionPosition pos) {
		return this.primitiveSections.get(pos);
	}
	
	/**
	 * Get primitive section at specified position.
	 * @param x Section X position.
	 * @param z Section Z position.
	 * @return The primitive section, or Null if no primitive section there.
	 */
	public WorldPrimitiveSection getPrimitiveSection(int x, int z) {
		try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
			return this.primitiveSections.get(pos.get().set(x, z));
		}
	}
	
	/**
	 * Get a region file from region position (region are groups of 32x32 sections).
	 * @param pos Region position ("sectionPosComponents" >> 5).
	 * @return The opened region file, should never return Null nor closed region file.
	 */
	public DimensionRegionFile getRegionFileCreate(AbsSectionPosition pos) {
		
		return this.regions.computeIfAbsent(pos.immutableSectionPos(), p -> {
			
			File file = new File(this.regionsDir, WorldTaskManager.getRegionFileName(p));
			
			try {
				
				if (file.isDirectory())
					throw new IllegalStateException("Can't create region file '" + file + "' because it's already a directory.");
				
				if (!file.isFile())
					file.createNewFile();
				
				return new DimensionRegionFile(file);
				
			} catch (IllegalStateException | IOException e) {
				
				LOGGER.log(Level.WARNING, "Failed to create a region file '" + file + "'.", e);
				return null;
				
			}
			
		});
		
	}
	
	/**
	 * Get a region file from region position (region are groups of 32x32 sections).
	 * @param pos Region position ("sectionPosComponents" >> 5).
	 * @return The opened region file (should never return closed region file), or Null if this region file is not opened.
	 */
	public DimensionRegionFile getRegionFile(AbsSectionPosition pos) {
		return this.regions.get(pos);
	}
	
	/**
	 * Get a region file from section position (region are groups of 32x32 sections).
	 * @param pos Section position.
	 * @param create True to create (or load) and initialize the region file if not cached.
	 * @return The opened region file, should never return closed region file but can return Null if 'create' parameter is set to False.
	 * @see #getRegionFileCreate(AbsSectionPosition)
	 * @see #getRegionFile(AbsSectionPosition)
	 */
	public DimensionRegionFile getSectionRegionFile(SectionPositioned pos, boolean create) {
		
		try (FixedObjectPool<SectionPosition>.PoolObject poolPos = SectionPosition.POOL.acquire()) {
			
			SectionPosition regpos = poolPos.get().set(pos.getX() >> 5, pos.getZ() >> 5);
			return create ? this.getRegionFileCreate(regpos) : this.getRegionFile(regpos);
			
		}
		
	}
	
	/**
	 * To know if a section is saved, can be used to know if the section have to be generated.
	 * @param pos Section position.
	 * @return True if the section is saved in the region file.
	 */
	public boolean isSectionSaved(SectionPositioned pos) {
		DimensionRegionFile file = this.getSectionRegionFile(pos, false);
		return file != null && file.isSectionSaved(pos.getX() & 31, pos.getZ() & 31);
	}
	
	/**
	 * Virtual world, only used to pass as parameter to chunk generator methods to
	 * avoid giving real world with potential unexpected behaviours.<br>
	 * It delegate calls to real worlds only if the used section is not primitive.
	 */
	private class VirtualLoaderWorld implements WorldAccessorServer {
		
		private final WorldDimension dim = DimensionLoader.this.dimension;
		
		/*
		public boolean inPrimitiveSection(SectionPosition pos) {
			return DimensionLoader.this.primitiveSections.containsKey(pos);
		}
		
		public boolean inPrimitiveSection(int x, int z) {
			try (FixedObjectPool<SectionPosition>.PoolObject pos = SectionPosition.POOL.acquire()) {
				return this.inPrimitiveSection(pos.get().set(x, z));
			}
		}
		*/

		@Override
		public long getSeed() {
			return this.dim.getSeed();
		}

		@Override
		public Random getRandom() {
			return this.dim.getRandom();
		}

		@Override
		public int getSeaLevel() {
			return this.dim.getSeaLevel();
		}

		@Override
		public WorldServerSection getSectionAt(AbsSectionPosition pos) {
			WorldPrimitiveSection p = DimensionLoader.this.getPrimitiveSection(pos);
			return p == null ? this.dim.getSectionAt(pos) : p;
		}
		
		@Override
		public WorldServerSection getSectionAt(int x, int z) {
			WorldPrimitiveSection p = DimensionLoader.this.getPrimitiveSection(x, z);
			return p == null ? this.dim.getSectionAt(x, z) : p;
		}
		
		@Override
		public boolean isSectionLoadedAt(int x, int z) {
			return this.dim.isSectionLoadedAt(x, z);
		}
		
		@Override
		public boolean isSectionLoadedAt(AbsSectionPosition pos) {
			return this.dim.isSectionLoadedAt(pos);
		}
		
		@Override
		public WorldServerChunk getChunkAt(int x, int y, int z) {
			WorldServerSection section = this.getSectionAt(x, z);
			return section == null ? null : section.getChunkAt(y);
		}

		@Override
		public short getHeightAt(Heightmap.Type type, int x, int z) {
			WorldServerSection section = (WorldServerSection) this.getSectionAtBlock(x, z);
			return section == null ? 0 : section.getHeightAt(type, x & 15, z & 15);
		}

		@Override
		public int getVerticalChunkCount() {
			return this.dim.getVerticalChunkCount();
		}

		@Override
		public int getHeightLimit() {
			return this.dim.getHeightLimit();
		}

		@Override
		public Biome getBiomeAt(int x, int z) {
			WorldSection section = this.getSectionAtBlock(x, z);
			return section == null ? null : section.getBiomeAtBlock(x, z);
		}
		
		@Override
		public BlockState getBlockAt(int x, int y, int z) {
			WorldChunk chunk = this.getChunkAtBlock(x, y, z);
			return chunk == null ? null : chunk.getBlockAt(x & 15, y & 15, z & 15);
		}
		
		@Override
		public void setBlockAt(int x, int y, int z, BlockState state) {
			
			// Need to delegate in a specific way because methods that modify the world
			// can send events, then an explicit delegate is needed instead of just
			// copying code.
			
			WorldPrimitiveSection p = DimensionLoader.this.getPrimitiveSection(x >> 4, z >> 4);
			
			if (p != null) {
				
				WorldChunk chunk = p.getChunkAtBlock(y);
				if (chunk != null) {
					chunk.setBlockAt(x & 15, y & 15, z & 15, state);
				}
				
			} else {
				this.dim.setBlockAt(x, y, z, state);
			}
			
		}
		
		@Override
		public boolean isBlockAt(int x, int y, int z, BlockState state) {
			WorldChunk chunk = this.getChunkAtBlock(x, y, z);
			return chunk != null && chunk.isBlockAt(x & 15, y & 15, z & 15, state);
		}
		
	}
	
}
