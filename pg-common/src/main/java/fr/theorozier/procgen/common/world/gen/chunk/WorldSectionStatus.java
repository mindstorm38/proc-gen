package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.world.chunk.WorldServerSection;

public abstract class WorldSectionStatus {
	
	public static final WorldSectionStatus EMPTY = new WorldSectionStatus("empty", null, BIOMES) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {}
	};
	
	public static final WorldSectionStatus BIOMES = new WorldSectionStatus("biomes", EMPTY, BASE) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genBiomes(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus BASE = new WorldSectionStatus("base", BIOMES, SURFACE) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSectionBase(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus SURFACE = new WorldSectionStatus("surface", BASE, FEATURES) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSurface(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FEATURES = new WorldSectionStatus("features", SURFACE, FINISHED) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genFeatures(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FINISHED = new WorldSectionStatus("finished", FEATURES, null) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {}
	};
	
	// Class //
	
	private final String identifier;
	private final WorldSectionStatus prev, next;
	
	public WorldSectionStatus(String identifier, WorldSectionStatus prev, WorldSectionStatus next) {
		
		this.identifier = identifier;
		this.prev = prev;
		this.next = next;
		
	}
	
	public final String getIdentifier() {
		return this.identifier;
	}
	
	public WorldSectionStatus getPrevious() {
		return this.prev;
	}
	
	public WorldSectionStatus getNext() {
		return this.next;
	}
	
	public boolean isFirst() {
		return this.prev == null;
	}
	
	public boolean isLast() {
		return this.next == null;
	}
	
	public abstract void generate(ChunkGenerator generator, WorldServerSection section);
	
}
