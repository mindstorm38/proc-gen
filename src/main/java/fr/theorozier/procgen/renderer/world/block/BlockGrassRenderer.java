package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockGrassRenderer extends BlockCubeRenderer {
	
	public BlockGrassRenderer() {
		super(null);
	}
	
	@Override
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("grass_top");
		} else if (face == Direction.BOTTOM) {
			return map.getTile("dirt");
		} else {
			return map.getTile("grass_side");
		}
		
	}
	
}
