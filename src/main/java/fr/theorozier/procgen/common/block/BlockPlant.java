package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

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
		return BlockRenderLayer.CUTOUT_NOT_CULLED;
	}
	
	@Override
	public void tickBlock(WorldServer world, BlockPositioned pos, BlockState block, Random rand) {
	
		/*
		if (rand.nextFloat() < 0.05f) {
			block.setBlockType(Blocks.DIAMOND_ORE);
		} else {
			world.getBlockTickList().scheduleTick(this, block.getPosition(), 20, TickPriority.NORMAL);
		}
		*/
	
	}
	
}
