package fr.theorozier.procgen.common.world.gen.beta;

import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.gen.DimensionHandler;
import fr.theorozier.procgen.common.world.gen.WorldIncompatException;

public class BetaDimensionHandler implements DimensionHandler {
	
	@Override
	public void worldLoaded(WorldDimensionManager dimensionManager) throws WorldIncompatException {
	
		throw new WorldIncompatException("...");
	
	}
	
}
