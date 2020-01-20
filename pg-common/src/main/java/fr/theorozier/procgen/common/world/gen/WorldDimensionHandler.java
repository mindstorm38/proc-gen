package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.gen.option.WorldGenerationOption;

public interface WorldDimensionHandler {
	
	void worldCreated(WorldDimensionManager dimensionManager, WorldGenerationOption generationOption);
	void worldLoaded(WorldDimensionManager dimensionManager) throws WorldIncompatException;
	
}
