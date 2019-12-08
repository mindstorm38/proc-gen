package fr.theorozier.procgen.block;

public class BlockFluidWater extends BlockFluid {
	
	public BlockFluidWater(int uid, String identifier) {
		super(uid, identifier);
	}
	
	@Override
	protected int getFluidViscosity() {
		return 10;
	}
	
}
