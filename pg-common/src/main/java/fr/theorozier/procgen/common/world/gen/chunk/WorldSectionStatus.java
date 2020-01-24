package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.world.chunk.WorldServerSection;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldSectionStatus {
	
	private static final Map<String, WorldSectionStatus> statusRegister = new HashMap<>();
	
	private static void registerStatus(WorldSectionStatus status) {
		statusRegister.put(status.getIdentifier(), status);
	}
	
	public static final WorldSectionStatus EMPTY = new WorldSectionStatus("empty", null, "biomes") {
		public void generate(ChunkGenerator generator, WorldServerSection section) {}
	};
	
	public static final WorldSectionStatus BIOMES = new WorldSectionStatus("biomes", "empty", "base") {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genBiomes(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus BASE = new WorldSectionStatus("base", "biomes", "surface") {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSectionBase(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus SURFACE = new WorldSectionStatus("surface", "base", "features") {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSurface(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FEATURES = new WorldSectionStatus("features", "surface", "finished") {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genFeatures(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FINISHED = new WorldSectionStatus("finished", "features", null) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {}
	};
	
	static {
		
		registerStatus(EMPTY);
		registerStatus(BIOMES);
		registerStatus(BASE);
		registerStatus(SURFACE);
		registerStatus(FEATURES);
		registerStatus(FINISHED);
		
		statusRegister.values().forEach(status -> {
			status.prev = status.prevId == null ? null : statusRegister.get(status.prevId);
			status.next = status.nextId == null ? null : statusRegister.get(status.nextId);
		});
		
	}
	
	// Class //
	
	private final String identifier;
	private final String prevId, nextId;
	private WorldSectionStatus prev, next;
	
	public WorldSectionStatus(String identifier, String prev, String next) {
		
		this.identifier = identifier;
		this.prevId = prev;
		this.nextId = next;
		
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
