package fr.theorozier.procgen.common.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class UnderwaterGravelSurface extends BiomeSurface {
	
	public UnderwaterGravelSurface() {
		
		super(3);
		
		this.addLayer(0, Blocks.GRAVEL);
		
	}
	
}
