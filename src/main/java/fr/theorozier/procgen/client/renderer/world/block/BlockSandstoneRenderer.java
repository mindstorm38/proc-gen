package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockSandstoneRenderer extends BlockCubeRenderer {
	
	public BlockSandstoneRenderer() {
		super(null);
	}
	
	@Override
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("sandstone_top");
		} else if (face == Direction.BOTTOM) {
			return map.getTile("sandstone_bottom");
		} else {
			return map.getTile("sandstone_side");
		}
		
	}
}
