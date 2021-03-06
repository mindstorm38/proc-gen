package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Common class used to store vertical arrays of chunks in a world.
 *
 * @author Theo Rozier
 *
 */
public class WorldSection {

	private final WorldBase world;
	private final ImmutableSectionPosition position;
	
	protected final Biome[] biomes;
	protected final WorldChunk[] chunks;
	
	public WorldSection(WorldBase world, SectionPositioned position) {
		
		this.world = world;
		this.position = position.immutableSectionPos();
		
		this.biomes = new Biome[256];
		this.chunks = new WorldChunk[world.getVerticalChunkCount()];
		
		Function<ImmutableBlockPosition, WorldChunk> chunkProvider = this.getChunkProvider();
		
		for (int y = 0; y < this.chunks.length; ++y)
			this.chunks[y] = chunkProvider.apply(new ImmutableBlockPosition(this.position, y));
		
	}
	
	protected Function<ImmutableBlockPosition, WorldChunk> getChunkProvider() {
		return pos -> new WorldChunk(this.world, this, pos);
	}
	
	// PROPERTIES //
	
	public WorldBase getWorld() {
		return this.world;
	}
	
	/**
	 * @return This section position (2,-1) -> block at (32,-15)
	 */
	public ImmutableSectionPosition getSectionPos() {
		return this.position;
	}
	
	// CHUNKS //
	
	/**
	 * Get chunk at specified Y coordinate, param = 2 for chunk at y = 32.
	 * @param y The Y coordinate of the chunk.
	 * @return The chunk a specified coordinate, or <b>NULL</b> if no chunk there.
	 */
	public WorldChunk getChunkAt(int y) {
		return y < 0 || y >= this.chunks.length ? null : this.chunks[y];
	}
	
	public WorldChunk getChunkAtBlock(int blockY) {
		return this.getChunkAt(blockY >> 4);
	}
	
	public void forEachChunk(Consumer<WorldChunk> cons) {
		
		for (int y = 0; y < this.chunks.length; y++)
			if (this.chunks[y] != null)
				cons.accept(this.chunks[y]);
			
	}
	
	// BIOMES //
	
	public Biome getBiomeAt(int x, int z) {
		return this.biomes[getSectionIndex(x, z)];
	}
	
	public Biome getBiomeAtBlock(int x, int z) {
		return this.getBiomeAt(x & 15, z & 15);
	}
	
	public Biome getBiomeAtBlock(SectionPositioned pos) {
		return this.getBiomeAtBlock(pos.getX(), pos.getZ());
	}
	
	public void setBiomeAt(int x, int z, Biome biome) {
		this.biomes[getSectionIndex(x, z)] = Objects.requireNonNull(biome);
	}
	
	public void setBiomeAtBlock(int x, int z, Biome biome) {
		this.setBiomeAt(x & 15, z & 15, biome);
	}
	
	public void setBiomes(Biome[] biomes) {
		
		if (biomes.length != 256)
			throw new IllegalArgumentException("Invalid biomes array length.");
		
		System.arraycopy(biomes, 0, this.biomes, 0, 256);
		
	}
	
	public Biome[] getBiomesData() {
		return this.biomes;
	}
	
	// BLOCKS //
	
	/**
	 * Get a block state at specific relative position to this section.
	 * @param x X coord (0 <= x < 16)
	 * @param y Y coord (0 <= x < max height)
	 * @param z Z coord (0 <= x < 16)
	 * @return The block state at this position, or <b>NULL</b> if no block saved there.
	 */
	public BlockState getBlockAt(int x, int y, int z) {
		WorldChunk chunk = this.getChunkAtBlock(y);
		return chunk == null ? null : chunk.getBlockAt(x, y & 15, z);
	}
	
	/**
	 * Set a block state at specific relative position to this section.
	 * @param x X coord (0 <= x < 16)
	 * @param y Y coord (0 <= x < max height)
	 * @param z Z coord (0 <= x < 16)
	 * @param state The state to save.
	 * @see WorldChunk#setBlockAt(int, int, int, BlockState)
	 */
	public void setBlockAt(int x, int y, int z, BlockState state) {
		WorldChunk chunk = this.getChunkAtBlock(y);
		if (chunk != null) chunk.setBlockAt(x, y & 15, z, state);
	}
	
	// UTILS //
	
	public static int getSectionIndex(int x, int z) {
		return (x << 4) | z;
		// return x * 16 + z;
	}
	
	public static int getSectionX(int index) {
		return (index >> 4) & 15;
	}
	
	public static int getSectionZ(int index) {
		return index & 15;
	}
	
}
