package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;

import java.util.List;

public class BlockCactus extends Block {
	
	public BlockCactus(String identifier) {
		super(identifier);
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
	public void getStateCollision(BlockState state, List<AxisAlignedBB> boundingBoxes) {
		boundingBoxes.add(new AxisAlignedBB(0.0625f, 0, 0.0625f, 0.9375, 1, 0.9375));
	}
	
}
