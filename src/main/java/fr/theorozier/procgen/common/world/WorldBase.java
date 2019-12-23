package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.HashMap;
import java.util.Map;

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
	
	protected final SectionPosition cachedSectionPos = new SectionPosition();
	protected final BlockPosition cachedBlockPos = new BlockPosition();
	
	public WorldBase() {
		
		this.time = 0L;
		
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
	
	// SECTIONS //
	
	/**
	 * Internal method to get a section at specific position.
	 * @param x The X section coordinate.
	 * @param z The Z section coordinate.
	 * @return Section at this position, or <b>NULL</b> if no section there.
	 */
	protected WorldSection getSectionAt(int x, int z) {
		this.cachedSectionPos.set(x, z);
		return this.sections.get(this.cachedSectionPos);
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
	
}
