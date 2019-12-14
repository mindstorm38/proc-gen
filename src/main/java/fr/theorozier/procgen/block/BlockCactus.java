package fr.theorozier.procgen.block;

public class BlockCactus extends Block {
	
	public BlockCactus(int uid, String identifier) {
		super(uid, identifier);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
}
