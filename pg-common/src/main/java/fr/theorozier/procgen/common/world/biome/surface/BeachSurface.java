package fr.theorozier.procgen.common.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class BeachSurface extends BiomeSurface {
	
	public BeachSurface() {
		
		super(3);
		
		this.addLayer(0, Blocks.SAND);
		
	}
	
}
