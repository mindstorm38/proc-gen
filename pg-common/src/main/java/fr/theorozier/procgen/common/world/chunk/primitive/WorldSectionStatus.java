package fr.theorozier.procgen.common.world.chunk.primitive;

import fr.theorozier.procgen.common.world.WorldAccessorServer;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldSectionStatus {
	
	private static final Map<String, WorldSectionStatus> statusRegister = new HashMap<>();
	
	private static void registerStatus(WorldSectionStatus status) {
		statusRegister.put(status.getIdentifier(), status);
	}
	
	// Generation //
	
	public static final WorldSectionStatus EMPTY = new WorldSectionStatus(0 ,"empty", "biomes", false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			// NOOP
		}
	};
	
	public static final WorldSectionStatus BIOMES = new WorldSectionStatus(1, "biomes", "base", false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			generator.genBiomes(primitivesAccessor, section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus BASE = new WorldSectionStatus(2,"base", "surface", false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			generator.genSectionBase(primitivesAccessor, section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus SURFACE = new WorldSectionStatus(3, "surface", "features", false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			generator.genSurface(primitivesAccessor, section, section.getSectionPos());
		}
	};
	
	public static final WorldSectionStatus FEATURES = new WorldSectionStatus(4, "features", "finished", true) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			generator.genFeatures(primitivesAccessor, section, section.getSectionPos());
		}
	};
	
	// Loading //
	
	public static final WorldSectionStatus LOADING = new WorldSectionStatus(0, "loading", "finished", false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {
			// NOOP
		}
	};
	
	// Finished //
	
	public static final WorldSectionStatus FINISHED = new WorldSectionStatus(1000, "finished", null, false) {
		public void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section) {}
	};
	
	static {
		
		registerStatus(EMPTY);
		registerStatus(BIOMES);
		registerStatus(BASE);
		registerStatus(SURFACE);
		registerStatus(FEATURES);
		
		registerStatus(LOADING);
		
		registerStatus(FINISHED);
		
        updateStatusNextIds();
		
	}

	public static void updateStatusNextIds() {

        statusRegister.values().forEach(status ->
				status.next = status.nextId == null ? null : statusRegister.get(status.nextId));

    }
	
	// Class //
	
	private final int order;
	private final String identifier;
	private final String nextId;
	private final boolean requireSameAround;
	private WorldSectionStatus next;
	
	public WorldSectionStatus(int order, String identifier, String nextId, boolean requireSameAround) {
		
		this.order = order;
		this.identifier = identifier;
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
	
	public WorldSectionStatus getNext() {
		return this.next;
	}
	
	public boolean isLast() {
		return this.next == null;
	}
	
	public boolean doRequireSameAround() {
		return this.requireSameAround;
	}
	
	public abstract void generate(ChunkGenerator generator, WorldAccessorServer primitivesAccessor, WorldServerSection section);
	
}
