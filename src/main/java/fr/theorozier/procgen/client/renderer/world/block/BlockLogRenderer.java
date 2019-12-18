package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockLogRenderer extends BlockCubeRenderer {
	
	public BlockLogRenderer() {
		super(null);
	}
	
	@Override
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		return face.axis == Blocks.LOG.getLogAxis(block) ? map.getTile("log_core") : map.getTile("log_bark");
	}
	
}
