package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

/**
 *
 * Basic handler for world dimensions saving.
 *
 */
public class WorldLoadingManager {
	
	private static final ThreadingDispatch WORLD_CHUNK_LOADING_DISPATCH = ThreadingDispatch.register("WORLD_CHUNK_LOADING", 3);
	
	private WorldDimensionManager dimensionManager;
	
	public boolean isSectionSaved(SectionPositioned pos) {
		return false;
	}
	
	public boolean isSectionSaving(SectionPositioned pos) {
		return false;
	}
	
	public void loadSectionTo(WorldServerSection section) {
	
	}
	
	public boolean isSectionLoading(SectionPositioned pos) {
		return false;
	}

}
