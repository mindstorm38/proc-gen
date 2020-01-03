package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.DefaultProperties;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockLogRenderer extends BlockCubeRenderer {
	
	public BlockLogRenderer() {
		super(null);
	}
	
	@Override
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
		return face.axis == block.get(DefaultProperties.AXIS) ? map.getTile("log_core") : map.getTile("log_bark");
	}
	
}
