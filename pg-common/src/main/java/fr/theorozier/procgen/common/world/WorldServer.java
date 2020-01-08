package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.gen.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.position.*;
import fr.theorozier.procgen.common.world.tick.WorldTickEntry;
import fr.theorozier.procgen.common.world.tick.WorldTickList;

import java.util.Random;

public class WorldServer extends WorldBase {
	
	public static final int NEAR_CHUNK_LOADING = 4;
	
	private final long seed;
	private final Random random;
	private final ChunkManager manager;
	
	private final WorldTickList<Block> blockTickList;
	private final int seaLevel;
	
	public WorldServer(long seed, ChunkGeneratorProvider provider) {
		
		this.seed = seed;
		this.random = new Random(seed);
		this.manager = new ChunkManager(this, provider.create(this));
		
		this.blockTickList = new WorldTickList<>(this, Block::isTickable, this::tickBlock);
		this.seaLevel = 63;
		
	}
	
	// PROPERTIES //
	
	public WorldServer(ChunkGeneratorProvider provider) {
		this(new Random().nextLong(), provider);
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public Random getRandom() {
		return this.random;
	}
	
	public ChunkManager getChunkManager() {
		return this.manager;
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
		
		this.manager.tick();
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
