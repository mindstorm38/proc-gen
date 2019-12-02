package fr.theorozier.procgen.world.biome.surface;

import fr.theorozier.procgen.block.Blocks;

public class GrassSurface extends BiomeSurface {
	
	public GrassSurface() {
		
		super(4);
		this.addLayer(0, Blocks.GRASS);
		this.addLayer(1, Blocks.DIRT);
		
	}
	
}
