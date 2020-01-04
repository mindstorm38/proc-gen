package fr.theorozier.procgen.common.block;

public class BlockAir extends Block {
	
	public BlockAir(int uid, String identifier) {
		
		super(uid, identifier);
		
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
