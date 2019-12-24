package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.gen.ChunkGenerator;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class WorldServerSection extends WorldSection {
	
	private final Map<Heightmap.Type, Heightmap> heightmaps;
	
	public WorldServerSection(WorldServer world, SectionPositioned position) {
		
		super(world, position);
		
		this.heightmaps = new EnumMap<>(Heightmap.Type.class);
		
	}
	
	public WorldServer getWorld() {
		return (WorldServer) super.getWorld();
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
	
	// FIXME TEMP MONO THREAD WORLD GENERATION METHOD
	public void generate() {
		
		ChunkGenerator generator = this.getWorld().getChunkManager().getGenerator();
		WorldServerChunk chunk;
		
		ImmutableSectionPosition pos = this.getSectionPos();
		generator.genBiomes(this, pos);
		
		for (int y = 0; y < this.getWorld().getVerticalChunkCount(); ++y) {
		
			chunk = new WorldServerChunk(this.getWorld(), this, new ImmutableBlockPosition(pos, y));
			this.setChunkAt(y, chunk);
			
			generator.genBase(chunk, chunk.getChunkPos());
			
		}
	
		generator.genSurface(this, pos);
		generator.genFeatures(this, pos);
		
		this.forEachChunk(loadedChunk -> {
			this.getWorld().getEventManager().fireListeners(WorldLoadingListener.class, l -> {
				l.worldChunkLoaded(this.getWorld(), loadedChunk);
			});
		});
		
	}
	
}
