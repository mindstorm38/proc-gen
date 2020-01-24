package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.world.chunk.WorldServerSection;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldSectionStatus {
	
	private static final Map<String, WorldSectionStatus> statusRegister = new HashMap<>();
	
	private static void registerStatus(WorldSectionStatus status) {
		statusRegister.put(status.getIdentifier(), status);
	}
	
	public static final WorldSectionStatus EMPTY = new WorldSectionStatus(0 ,"empty", null, "biomes", false) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {}
	};
	
	public static final WorldSectionStatus BIOMES = new WorldSectionStatus(1, "biomes", "empty", "base", false) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genBiomes(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus BASE = new WorldSectionStatus(2,"base", "biomes", "surface", false) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSectionBase(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus SURFACE = new WorldSectionStatus(3, "surface", "base", "features", false) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genSurface(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FEATURES = new WorldSectionStatus(4, "features", "surface", "finished", true) {
		public void generate(ChunkGenerator generator, WorldServerSection section) {
			generator.genFeatures(section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FINISHED = new WorldSectionStatus(1000, "finished", "features", null, false) {
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
	
	private final int order;
	private final String identifier;
	private final String prevId, nextId;
	private final boolean requireSameAround;
	private WorldSectionStatus prev, next;
	
	public WorldSectionStatus(int order, String identifier, String prevId, String nextId, boolean requireSameAround) {
		
		this.order = order;
		this.identifier = identifier;
		this.prevId = prevId;
		this.nextId = nextId;
		this.requireSameAround = requireSameAround;
		
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public boolean isAsLeastAt(WorldSectionStatus status) {
		return this.order >= status.order;
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
	
	public boolean doRequireSameAround() {
		return this.requireSameAround;
	}
	
	public abstract void generate(ChunkGenerator generator, WorldServerSection section);
	
}
