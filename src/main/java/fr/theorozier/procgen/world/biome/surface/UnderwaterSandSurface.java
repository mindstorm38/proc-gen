package fr.theorozier.procgen.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class UnderwaterSandSurface extends BiomeSurface {
	
	public UnderwaterSandSurface() {
		
		super(2);
		this.addLayer(0, Blocks.SAND);
		
	}
	
}
