package fr.theorozier.procgen.common.block;

public class BlockAir extends Block {
	
	public BlockAir(String identifier) {
		super(identifier);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean isUnsavable() {
		return true;
	}
	
}
