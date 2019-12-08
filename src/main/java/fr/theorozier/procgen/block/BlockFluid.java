package fr.theorozier.procgen.block;

public abstract class BlockFluid extends Block {
	
	public BlockFluid(int uid, String identifier) {
		super(uid, identifier);
	}
	
	protected abstract int getFluidViscosity();
	
}
