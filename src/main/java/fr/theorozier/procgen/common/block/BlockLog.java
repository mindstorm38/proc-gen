package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.BlockStateContainer;
import fr.theorozier.procgen.common.block.state.DefaultProperties;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.Axis;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

public class BlockLog extends Block {
	
	public BlockLog(int uid, String identifier) {
		
		super(uid, identifier);
		
		this.setDefaultState(this.getDefaultState().with(DefaultProperties.AXIS, Axis.Y));
		
	}
	
	@Override
	public void registerStateContainerProperties(BlockStateContainer.Builder builder) {
		builder.register(DefaultProperties.AXIS);
	}
	
	@Override
	public void initBlock(WorldServer world, BlockPositioned pos, BlockState block) {
	
	}
	
}
