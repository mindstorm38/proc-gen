package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.gen.option.WorldGenerationOption;

public interface WorldDimensionHandler {
	
	void worldCreated(WorldServer dimensionManager, WorldGenerationOption generationOption);
	void worldLoaded(WorldServer dimensionManager) throws WorldIncompatException;
	
}
