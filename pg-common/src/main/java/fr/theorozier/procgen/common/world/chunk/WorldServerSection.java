package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.chunk.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class WorldServerSection extends WorldSection {
	
	protected final Map<Heightmap.Type, Heightmap> heightmaps;
	
	public WorldServerSection(WorldDimension world, SectionPositioned position) {
		
		super(world, position);
		
		this.heightmaps = new EnumMap<>(Heightmap.Type.class);
		
	}
	
	public WorldServerSection(WorldPrimitiveSection primitiveSection) {
		
		super(primitiveSection.getWorld(), primitiveSection.getSectionPos());
		
		primitiveSection.forEachChunk(chunk -> this.chunks[chunk.getChunkPos().getY()] = chunk);
		System.arraycopy(primitiveSection.biomes, 0, this.biomes, 0, 256);
		
		this.heightmaps = primitiveSection.heightmaps;
		
	}
	
	public WorldDimension getWorld() {
		return (WorldDimension) super.getWorld();
	}
	
	// CHUNKS //
	
	public WorldServerChunk getChunkAt(int y) {
		return (WorldServerChunk) super.getChunkAt(y);
	}
	
	public WorldServerChunk getChunkAtBlock(int blockY) {
		return (WorldServerChunk) super.getChunkAtBlock(blockY);
	}
	
	// HEIGHTMAPS //
	
	public Heightmap getHeightmap(Heightmap.Type type) {
		return this.heightmaps.computeIfAbsent(type, tp -> new Heightmap(this, tp));
	}
	
	/**
	 * Get height at a specific horizontal position and a type of collision type.<br>
	 * If this height was not existing, creating it and initializing it from current section state.
	 * @param type The type of collision.
	 * @param x The X coord.
	 * @param z The Z coord.
	 * @return The height.
	 */
	public short getHeightAt(Heightmap.Type type, int x, int z) {
		
		Heightmap map = this.heightmaps.get(type);
		
		if (map == null) {
			
			map = new Heightmap(this, type);
			this.heightmaps.put(type, map);
			
			Heightmap.updateSectionHeightmaps(this, EnumSet.of(type));
			
		}
		
		return map.get(x, z);
		
	}
	
	/**
	 * Force recompute height map of specified type.
	 * @param types Heightmap types set to recompute.
	 */
	public void recomputeHeightmap(Set<Heightmap.Type> types) {
		Heightmap.updateSectionHeightmaps(this, types);
	}
	
	/**
	 * Force recompute height map of specified type.
	 * @param type The heightmap type to recompute.
	 */
	public void recomputeHeightmap(Heightmap.Type type) {
		this.recomputeHeightmap(EnumSet.of(type));
	}
	
}
