package fr.theorozier.procgen.common.world.feature.config;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.function.Predicate;

public class PlantFeatureConfig implements FeatureConfig {
	
	private final BlockState plantBlock;
	private final Predicate<BlockState> canPlaceOn;
	
	public PlantFeatureConfig(BlockState plantBlock, Predicate<BlockState> canPlaceOn) {
		
		this.plantBlock = plantBlock;
		this.canPlaceOn = canPlaceOn;
		
	}
	
	public PlantFeatureConfig(Block block,  Predicate<BlockState> canPlaceOn) {
		this(block.getDefaultState(), canPlaceOn);
	}
	
	public BlockState getPlantBlock() {
		return this.plantBlock;
	}
	
	public boolean canPlaceOn(BlockState block) {
		return block != null && this.canPlaceOn.test(block);
	}
	
}
