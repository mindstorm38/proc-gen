package fr.theorozier.procgen.common.world.biome.surface;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BiomeSurface {
	
	private final short baseHeight;
	private final Map<Short, BlockState> layers;
	
	public BiomeSurface(int baseHeight) {
		
		this.baseHeight = (short) baseHeight;
		this.layers = new HashMap<>();
		
	}
	
	public void addLayer(int layerFromTop, BlockState block) {
		this.layers.put((short) layerFromTop, block);
	}
	
	public void addLayer(int layerFromTop, Block block) {
		this.addLayer(layerFromTop, block.getDefaultState());
	}
	
	public short getBaseHeight() {
		return this.baseHeight;
	}
	
	public Map<Short, BlockState> getLayers() {
		return this.layers;
	}
	
	public BlockState getLayer(short layerFromTop) {
		return this.layers.get(layerFromTop);
	}

}
