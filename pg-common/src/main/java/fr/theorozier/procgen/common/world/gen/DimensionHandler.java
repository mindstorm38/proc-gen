package fr.theorozier.procgen.common.world.gen;

import fr.theorozier.procgen.common.world.WorldDimensionManager;

public interface DimensionHandler {
	
	void worldLoaded(WorldDimensionManager dimensionManager) throws WorldIncompatException;
	
}
