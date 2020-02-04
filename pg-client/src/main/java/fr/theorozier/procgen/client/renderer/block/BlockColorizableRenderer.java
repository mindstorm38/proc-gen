package fr.theorozier.procgen.client.renderer.block;

public abstract class BlockColorizableRenderer extends BlockRenderer {
	
	protected final boolean needColorization;
	
	public BlockColorizableRenderer(boolean needColorization) {
		this.needColorization = needColorization;
	}
	
}
