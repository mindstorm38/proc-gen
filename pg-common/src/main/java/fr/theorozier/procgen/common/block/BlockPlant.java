package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.List;
import java.util.Random;

public class BlockPlant extends Block {
	
	public BlockPlant(int uid, String identifier) {
		super(uid, identifier);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public void getStateCollision(BlockState state, List<AxisAlignedBB> boundingBoxes) {}
	
	@Override
	public void tickBlock(WorldDimension world, BlockPositioned pos, BlockState block, Random rand) {
	
		/*
		if (rand.nextFloat() < 0.05f) {
			block.setBlockType(Blocks.DIAMOND_ORE);
		} else {
			world.getBlockTickList().scheduleTick(this, block.getPosition(), 20, TickPriority.NORMAL);
		}
		*/
	
	}
	
}
