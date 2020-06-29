package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockStateContainer;
import fr.theorozier.procgen.common.block.state.DefaultProperties;
import fr.theorozier.procgen.common.world.position.Axis;

public class BlockLog extends Block {
	
	public BlockLog(String identifier) {
		
		super(identifier);
		
		this.setDefaultState(this.getDefaultState().with(DefaultProperties.AXIS, Axis.Y));
		
	}
	
	@Override
	public void registerStateContainerProperties(BlockStateContainer.Builder builder) {
		builder.register(DefaultProperties.AXIS);
	}
	
}
