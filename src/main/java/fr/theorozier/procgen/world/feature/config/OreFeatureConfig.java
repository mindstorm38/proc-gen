package fr.theorozier.procgen.world.feature.config;

import fr.theorozier.procgen.block.Block;

public class OreFeatureConfig implements FeatureConfig {

	private final Block ore;
	private final int infCount;
	private final int supCount;
	
	public OreFeatureConfig(Block ore, int infCount, int supCount) {
		
		this.ore = ore;
		this.infCount = infCount;
		this.supCount = supCount;
		
	}
	
	public Block getOre() {
		return this.ore;
	}
	
	public int getInfCount() {
		return this.infCount;
	}
	
	public int getSupCount() {
		return this.supCount;
	}

}
