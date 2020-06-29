package fr.theorozier.procgen.common.block;

public class BlockFluidWater extends BlockFluid {
	
	public BlockFluidWater(String identifier) {
		super(identifier);
	}
	
	@Override
	protected int getFluidViscosity() {
		return 10;
	}
	
	@Override
	public float getInnerFriction() {
		return 1f;
	}
	
}
