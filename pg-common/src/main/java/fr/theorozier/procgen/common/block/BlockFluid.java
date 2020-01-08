package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;

import java.util.List;

public abstract class BlockFluid extends Block {
	
	public BlockFluid(int uid, String identifier) {
		super(uid, identifier);
	}
	
	protected abstract int getFluidViscosity();
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSPARENT;
	}
	
	@Override
	public void getStateCollision(BlockState state, List<AxisAlignedBB> boundingBoxes) {}
	
	@Override
	public abstract float getInnerViscosity();
	
}
