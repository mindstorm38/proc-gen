package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import io.msengine.common.util.Color;

public abstract class BlockColorizableRenderer extends BlockRenderer {
	
	protected final boolean needColorization;
	
	public BlockColorizableRenderer(boolean needColorization) {
		this.needColorization = needColorization;
	}
	
	public abstract Color getColorization(WorldBase world, BlockState block, int x, int y, int z);
	
}
