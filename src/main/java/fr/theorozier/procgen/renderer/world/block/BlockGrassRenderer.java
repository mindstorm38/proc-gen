package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockGrassRenderer extends BlockCubeRenderer {
	
	public BlockGrassRenderer() {
		super("grass_side");
	}
	
	@Override
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("grass_top");
		} else if (face == Direction.BOTTOM) {
			return map.getTile("dirt");
		} else {
			return super.getFaceTile(block, map, face);
		}
		
	}
}
