package fr.theorozier.procgen.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class UnderwaterDirtSurface extends BiomeSurface {
	
	public UnderwaterDirtSurface() {
		
		super(3);
		this.addLayer(0, Blocks.DIRT);
		
	}
	
}
