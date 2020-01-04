package fr.theorozier.procgen.common.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class GrassSurface extends BiomeSurface {
	
	public GrassSurface() {
		
		super(4);
		
		this.addLayer(0, Blocks.GRASS);
		this.addLayer(1, Blocks.DIRT);
		
	}
	
}
