package fr.theorozier.procgen.world.feature.config;

import fr.theorozier.procgen.block.Block;

import java.util.function.Predicate;

public class PlantFeatureConfig implements FeatureConfig {
	
	private final Block plantBlock;
	private final Predicate<Block> canPlaceOn;
	
	public PlantFeatureConfig(Block plantBlock, Predicate<Block> canPlaceOn) {
		
		this.plantBlock = plantBlock;
		this.canPlaceOn = canPlaceOn;
		
	}
	
	public Block getPlantBlock() {
		return this.plantBlock;
	}
	
	public boolean canPlaceOn(Block block) {
		return this.canPlaceOn.test(block);
	}
	
}
