package fr.theorozier.procgen.block;

public class BlockAir extends Block {
	
	public BlockAir(int uid, String identifier) {
		
		super(uid, identifier);
		
		this.opaque = false;
		
	}
	
	@Override
	public boolean isUnsavable() {
		return true;
	}
	
}
