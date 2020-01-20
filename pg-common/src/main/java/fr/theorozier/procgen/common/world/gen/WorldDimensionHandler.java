package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.gen.option.WorldGenerationOption;

public interface WorldDimensionHandler<O extends WorldGenerationOption> {
	
	void worldCreated(WorldDimensionManager dimensionManager, O generationOption);
	void worldLoaded(WorldDimensionManager dimensionManager) throws WorldIncompatException;
	
	@SuppressWarnings("unchecked")
	default void worldCreatedRaw(WorldDimensionManager dimensionManager, WorldGenerationOption option) {
		
		try {
			this.worldCreated(dimensionManager, (O) option);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Invalid option class for this dimension handler.");
		}
		
	}
	
}
