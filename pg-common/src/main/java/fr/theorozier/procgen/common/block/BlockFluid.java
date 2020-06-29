package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

import java.util.List;

public abstract class BlockFluid extends Block {
	
	public BlockFluid(String identifier) {
		super(identifier);
	}
	
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
	
	// Properties
	
	@Override
	public abstract float getInnerFriction();
	
	@Override
	public boolean canOverride(WorldDimension world, BlockPositioned pos, BlockState state) {
		return true;
	}
	
	protected abstract int getFluidViscosity();
	
}
