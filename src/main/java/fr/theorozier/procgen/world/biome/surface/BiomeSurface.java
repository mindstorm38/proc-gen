package fr.theorozier.procgen.world.biome.surface;

import fr.theorozier.procgen.common.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BiomeSurface {
	
	private final short baseHeight;
	private final Map<Short, Block> layers;
	
	public BiomeSurface(int baseHeight) {
		
		this.baseHeight = (short) baseHeight;
		this.layers = new HashMap<>();
		
	}
	
	public void addLayer(int layerFromTop, Block block) {
		this.layers.put((short) layerFromTop, block);
	}
	
	public short getBaseHeight() {
		return this.baseHeight;
	}
	
	public Map<Short, Block> getLayers() {
		return this.layers;
	}
	
	public Block getLayer(short layerFromTop) {
		return this.layers.get(layerFromTop);
	}

}
