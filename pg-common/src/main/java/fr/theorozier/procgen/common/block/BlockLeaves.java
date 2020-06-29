package fr.theorozier.procgen.common.block;

public class BlockLeaves extends Block {
	
	public BlockLeaves(String identifier) {
		super(identifier);
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
