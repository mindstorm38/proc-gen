package fr.theorozier.procgen.world.chunk;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.HorizontalPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.biome.Biome;
import fr.theorozier.procgen.world.biome.BiomeAccessor;
import fr.theorozier.procgen.world.biome.Biomes;
import fr.theorozier.procgen.world.gen.ChunkGenerator;

import java.util.*;

/**
 *
 * A section is a column of same X,Z chunks.<br>
 * The section also manage generation of all chunks in it.
 *
 * @author Theo Rozier
 *
 */
public class Section implements BiomeAccessor {
	
	private final World world;
	private final SectionPosition position;
	private final ChunkGenerator generator;
	
	private boolean generated;
	
	private final Map<Integer, Chunk> chunks;
	private Biome[] biomes;
	
	private final Map<Heightmap.Type, Heightmap> heightmaps;
	
	public Section(World world, SectionPosition position, ChunkGenerator generator) {
		
		this.world = world;
		this.position = position;
		this.generator = generator;
		
		this.generated = false;
		
		this.chunks = new HashMap<>();
		this.biomes = new Biome[256];
		Arrays.fill(this.biomes, Biomes.EMPTY);
		
		this.heightmaps = new EnumMap<>(Heightmap.Type.class);
		
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public SectionPosition getSectionPosition() {
		return this.position;
	}
	
	public boolean wasGenerated() {
		return this.generated;
	}
	
	////////////////////////
	// Position Utilities //
	////////////////////////
	
	public void validatePosition(int x, int z) {
		
		if (!this.position.isInSection(x, z))
			throw new IllegalArgumentException("Invalid section position, for this section.");
		
	}
	
	public void validatePosition(SectionPosition pos) {
		this.validatePosition(pos.getX(), pos.getZ());
	}
	
	/**
	 * Internal method to get the position index in a 2D linear array of square size 16.
	 * @param x Relative position X.
	 * @param z Relative position Z.
	 * @return Position index.
	 */
	public static short getHorizontalPositionIndex(int x, int z) {
		return (short) (x + z * 16);
	}
	
	////////////////
	// Generation //
	////////////////
	
	public void generate() {
	
		if (this.wasGenerated())
			throw new IllegalStateException("Already generated.");
		
		Chunk chunk;
		
		this.generator.genBiomes(this, this.position);
		
		for (int y = 0; y < this.world.getSectionHeightLimit(); ++y) {
			
			chunk = new Chunk(this, this.position.getChunkPos(y * 16));
			this.chunks.put(y, chunk);
			
			this.generator.genBase(chunk, chunk.getChunkPosition());
			
		}
		
		this.generator.genSurface(this, this.position);
		this.generator.genFeatures(this, this.position);
		
		for (int y = 0; y < this.world.getSectionHeightLimit(); ++y)
			this.world.triggerChunkLoadedListeners(this.chunks.get(y));
		
		this.generated = true;
		
	}
	
	///////////////////////////////
	// Chunks & Blocks Accessing //
	///////////////////////////////
	
	public Chunk getChunkAt(int y) {
		return this.chunks.get(y >> 4);
	}
	
	
	////////////////
	// Heightmaps //
	////////////////
	
	public Heightmap getHeightmap(Heightmap.Type type) {
		return this.heightmaps.computeIfAbsent(type, tp -> new Heightmap(this, tp));
	}
	
	public short getHeightAt(Heightmap.Type type, int x, int z) {
		
		Heightmap map = this.heightmaps.get(type);
		
		if (map == null) {
			
			Heightmap.updateSectionHeightmaps(this, EnumSet.of(type));
			map = this.heightmaps.get(type);
			
		}
		
		return map.get(x & 15, z & 15);
		
	}
	
	public short getHeightAt(Heightmap.Type type, HorizontalPosition pos) {
		return this.getHeightAt(type, pos.getX(), pos.getZ());
	}
	
	/////////////////////
	// Biome Accessing //
	/////////////////////
	
	/**
	 * Set the array of biomes.
	 * @param biomes New biomes array, must have a length of 256.
	 */
	public void setBiomes(Biome[] biomes) {
		
		if (biomes.length != 256)
			throw new IllegalArgumentException("Invalid biomes array length, must have chunk section surface length (256).");
		
		this.biomes = biomes;
		
	}
	
	/**
	 * Get biome at relative position in this chunk.
	 * @param x The X relative position.
	 * @param z The Y relative position.
	 * @return The biome at this block position.
	 */
	public Biome getBiomeAtRelative(int x, int z) {
		return this.biomes[getHorizontalPositionIndex(x, z)];
	}
	
	@Override
	public Biome getBiomeAt(int x, int z) {
		this.validatePosition(x, z);
		return this.getBiomeAtRelative(x - this.position.getX(), z - this.position.getZ());
	}
	
	@Override
	public Biome getBiomeAt(SectionPosition pos) {
		this.validatePosition(pos);
		return this.getBiomeAtRelative(pos.getX() - this.position.getX(), pos.getZ() - this.position.getZ());
	}
	
}
