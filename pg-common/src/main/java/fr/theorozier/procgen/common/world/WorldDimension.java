package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.event.WorldEntityListener;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.AbsSectionPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import fr.theorozier.procgen.common.world.load.DimensionLoader;
import fr.theorozier.procgen.common.world.load.DimensionMetadata;
import fr.theorozier.procgen.common.world.load.WorldLoadingPosition;
import fr.theorozier.procgen.common.world.task.WorldTaskManager;
import fr.theorozier.procgen.common.world.task.WorldTaskType;
import fr.theorozier.procgen.common.world.chunk.primitive.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.tick.WorldTickEntry;
import fr.theorozier.procgen.common.world.tick.WorldTickList;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 *
 * A server-side world class, to manage all world's logic.
 * World servers requires to be instantiated from {@link WorldServer}, so WorldServer are also called 'Dimension'.
 *
 * @author Theo Rozier
 *
 */
public class WorldDimension extends WorldBase implements WorldAccessorServer {
	
	public static final int NEAR_CHUNK_LOADING = 4;
	
	private final WorldServer world;
	private final String identifier;
	private final File directory;
	
	private final DimensionMetadata metadata;

	private final long seed;
	private final Random random;

	private final WorldTickList<Block> blockTickList;
	private final int seaLevel;
	
	private final DimensionLoader loader;
	private final HashSet<WorldLoadingPosition> worldLoadingPositions = new HashSet<>();

	// TODO: Create a special world view, only used for generation and implementing WorldAccessor.
	
	public WorldDimension(WorldServer world, String identifier, File directory, DimensionMetadata metadata) {
		
		this.world = Objects.requireNonNull(world);
		this.identifier = Objects.requireNonNull(identifier);
		this.directory = Objects.requireNonNull(directory);
		
		this.metadata = Objects.requireNonNull(metadata);

		this.time = this.metadata.getTime();
		
		this.seed = this.metadata.getSeed();
		this.random = new Random(this.seed);

		this.blockTickList = new WorldTickList<>(this, Block::isTickable, this::tickBlock);
		this.seaLevel = 63;
		
		this.loader = new DimensionLoader(this);

	}
	
	// PROPERTIES //

	/**
	 * @return The underlying dimension manager.
	 */
	public WorldServer getWorld() {
		return this.world;
	}

	/**
	 * @return The dimension identifier, given by {@link WorldServer}.
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * @return The dimension directory where all world specific data are saved, like regions files.
	 */
	public File getDirectory() {
		return this.directory;
	}
	
	/**
	 * Used to update dynamics metadata values :<br>
	 * <ul>
	 *     <li>Time (ticks elapsed since world creation, used for day cycle)</li>
	 * </ul>
	 * Update these dynamic values in the metadata will not update them in the dimension
	 * since they are only used for metadata serialization or on world instantiation.
	 */
	public void updateMetadataDynamics() {
		this.metadata.setTime(this.time);
	}
	
	/**
	 * Get this dimension metadata, but update dynamic values using {@link #updateMetadataDynamics()}.
	 * @return The unique dimension metadata.
	 * @see #updateMetadataDynamics()
	 */
	public DimensionMetadata getMetadata() {
		this.updateMetadataDynamics();
		return this.metadata;
	}
	
	/**
	 * Dynamically refresh metadata from new one, it only works for dynamic values (see {@link #getMetadata()}).
	 * @param metadata The new metadata, internal instance is not replaced but dynamic values are copied from the new one.
	 * @see #getMetadata()
	 */
	public void refreshMetadata(DimensionMetadata metadata) {
		this.metadata.setDynamics(metadata);
		this.time = metadata.getTime();
	}

	/**
	 * @return Used dimension loader.
	 */
	public DimensionLoader getLoader() {
		return this.loader;
	}

	@Override
	public long getSeed() {
		return this.seed;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}
	
	@Override
	public boolean isServer() {
		return true;
	}
	
	@Override
	public WorldDimension getAsServer() {
		return this;
	}

	@Override
	public int getSeaLevel() {
		return this.seaLevel;
	}

	/**
	 * @return Internal loading manager.
	 * @throws IllegalStateException If the loading manager is not available.
	 */
	public WorldTaskManager getTaskManager() {
		return this.world.getTaskManager().orElseThrow(() -> new IllegalStateException("Can't load chunks if task manager not initialized on world."));
	}

	// TICKING //
	
	@Override
	public void update() {
		
		super.update();
		
		PROFILER.startSection("loading_pos");
		this.updateChunkLoadingPositions();
		
		PROFILER.endStartSection("loader");
		this.loader.update();

		PROFILER.endStartSection("block_ticks");
		this.blockTickList.tick();
		
		PROFILER.endStartSection("entities");
		this.updateEntities();
		
		PROFILER.endSection();
		
	}

	/**
	 * Update all entities in the world.
	 */
	public void updateEntities() {
	
		for (int i = 0; i < this.entities.size(); ++i) {
			
			Entity entity = this.entities.get(i);
			
			if (!entity.isDead()) {
				this.updateEntity(entity);
			}
			
			if (entity.isDead()) {
				
				int ecx = entity.getChunkPosX();
				int ecy = entity.getChunkPosY();
				int ecz = entity.getChunkPosZ();
				
				if (entity.isInChunk() && this.isSectionLoadedAt(ecx, ecz)) {
					this.getChunkAt(ecx, ecy, ecz).removeEntity(entity);
				}
				
				this.entitiesById.remove(entity.getUid());
				this.entities.remove(i--);
				
				this.eventManager.fireListeners(WorldEntityListener.class, l -> l.worldEntityRemoved(this, entity));
				
			}
			
		}
	
	}

	/**
	 * Update a specific entity, it also check for chunk positions and relocate the entity in another chunk if needed.
	 * @param entity The entity to update.
	 */
	public void updateEntity(Entity entity) {
		
		int ex = entity.getCurrentChunkPosX();
		int ey = entity.getCurrentChunkPosY();
		int ez = entity.getCurrentChunkPosZ();
		
		int ecx = entity.getChunkPosX();
		int ecy = entity.getChunkPosY();
		int ecz = entity.getChunkPosZ();
		
		if (!entity.isInChunk() || ex != ecx || ey != ecy || ez != ecz) {
			
			if (entity.isInChunk() && this.isSectionLoadedAt(ecx, ecz)) {
				this.getChunkAt(ecx, ecy, ecz).removeEntity(entity);
			}
			
			if (this.isSectionLoadedAt(ex, ez)) {
				this.getChunkAt(ex, ey, ez).addEntity(entity);
			} else {
				entity.setInChunk(false);
			}
			
		}
		
		if (entity.isInChunk()) {
			entity.update();
		}
	
	}
	
	/**
	 * Check if a tick can be triggered at a position.
	 * @param pos The position to check.
	 * @return True if you can tick at this position.
	 */
	public boolean canTickAt(BlockPositioned pos) {
		return this.isSectionLoadedAt(pos.getX(), pos.getZ());
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
	
	// ENTITIES //
	
	public boolean spawnEntity(Entity entity) {
		
		if (this.entitiesById.containsKey(entity.getUid()))
			throw new IllegalStateException("This entity is already loaded in the world.");
		
		int ex = entity.getCurrentChunkPosX();
		int ey = entity.getCurrentChunkPosY();
		int ez = entity.getCurrentChunkPosZ();
		
		if (this.isSectionLoadedAt(ex, ez)) {
			
			this.getChunkAt(ex, ey, ez).addEntity(entity);
			this.entitiesById.put(entity.getUid(), entity);
			this.entities.add(entity);
			
			this.eventManager.fireListeners(WorldEntityListener.class, l -> l.worldEntityAdded(this, entity));
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
	// SECTIONS //
	
	@Override
	public WorldServerSection getSectionAt(int x, int z) {
		return (WorldServerSection) super.getSectionAt(x, z);
	}
	
	@Override
	public WorldServerSection getSectionAt(AbsSectionPosition pos) {
		return (WorldServerSection) super.getSectionAt(pos);
	}
	
	@Override
	public WorldServerSection getSectionAtBlock(int x, int z) {
		return (WorldServerSection) super.getSectionAtBlock(x, z);
	}

	@Override
	public WorldServerSection getSectionAtBlock(AbsBlockPosition pos) {
		return (WorldServerSection) super.getSectionAtBlock(pos);
	}

	// CHUNKS //
	
	@Override
	public WorldServerChunk getChunkAt(int x, int y, int z) {
		return (WorldServerChunk) super.getChunkAt(x, y, z);
	}
	
	@Override
	public WorldServerChunk getChunkAt(AbsBlockPosition pos) {
		return (WorldServerChunk) super.getChunkAt(pos);
	}
	
	@Override
	public WorldServerChunk getChunkAtBlock(int x, int y, int z) {
		return (WorldServerChunk) super.getChunkAtBlock(x, y, z);
	}
	
	@Override
	public WorldServerChunk getChunkAtBlock(AbsBlockPosition pos) {
		return (WorldServerChunk) super.getChunkAtBlock(pos);
	}
	
	// HEIGHTMAPS //

	@Override
	public short getHeightAt(Heightmap.Type type, int x, int z) {
		WorldServerSection section = this.getSectionAtBlock(x, z);
		return section == null ? 0 : section.getHeightAt(type, x & 15, z & 15);
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
	
	public int getDistanceToLoaders(SectionPositioned sectionPos) {
		
		return this.worldLoadingPositions.stream()
				.mapToInt(sp -> (int) sp.distSquared(sectionPos.getX() << 4, sectionPos.getZ() << 4))
				.min()
				.orElse(0);
		
	}

	public void loadPrimitiveSection(WorldPrimitiveSection primitiveSection, WorldTaskType type) {
		
		PROFILER.startSection("new_obj_and_add");
		WorldServerSection newSection = new WorldServerSection(primitiveSection);
		this.sections.put(primitiveSection.getSectionPos(), newSection);
		
		PROFILER.endStartSection("listeners");
		newSection.forEachChunk(chunk ->
				this.eventManager.fireListeners(WorldLoadingListener.class, l ->
						l.worldChunkLoaded(this, chunk)
				)
		);
		PROFILER.endSection();
		
		if (type == WorldTaskType.GENERATE) {
			
			PROFILER.startSection("request_save");
			this.loader.saveSectionAfter(newSection.getSectionPos());
			PROFILER.endSection();
			
		}
		
	}
	
	private void tryLoadSection(SectionPosition sectionPosition) {

		if (!this.isSectionLoadedAt(sectionPosition)) {
			this.loader.loadSection(sectionPosition);
		}
	
	}

	/*
	@Deprecated
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
	*/
	
}
