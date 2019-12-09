package fr.theorozier.procgen.block;

public class BlockLeaves extends Block {
	
	public BlockLeaves(int uid, String identifier) {
		
		super(uid, identifier);
		
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean mustRenderSameBlockFaces() {
		return true;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
}
