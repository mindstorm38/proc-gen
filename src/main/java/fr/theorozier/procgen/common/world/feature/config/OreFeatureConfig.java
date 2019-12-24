package fr.theorozier.procgen.common.world.feature.config;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;

public class OreFeatureConfig implements FeatureConfig {

	private final BlockState ore;
	private final int infCount;
	private final int supCount;
	
	public OreFeatureConfig(BlockState ore, int infCount, int supCount) {
		
		this.ore = ore;
		this.infCount = infCount;
		this.supCount = supCount;
		
	}
	
	public OreFeatureConfig(Block block, int infCount, int supCount) {
		this(block.getDefaultState(), infCount, supCount);
	}
	
	public BlockState getOre() {
		return this.ore;
	}
	
	public int getInfCount() {
		return this.infCount;
	}
	
	public int getSupCount() {
		return this.supCount;
	}

}
