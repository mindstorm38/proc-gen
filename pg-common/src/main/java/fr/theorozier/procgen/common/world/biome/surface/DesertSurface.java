package fr.theorozier.procgen.common.world.biome.surface;

import fr.theorozier.procgen.common.block.Blocks;

public class DesertSurface extends BiomeSurface {
	
	public DesertSurface() {
		
		super(8);
		
		this.addLayer(0, Blocks.SAND);
		this.addLayer(4, Blocks.SANDSTONE);
		
	}
	
}
