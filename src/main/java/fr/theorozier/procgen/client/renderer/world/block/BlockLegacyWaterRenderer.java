package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockLegacyWaterRenderer extends BlockCubeRenderer {
	
	public BlockLegacyWaterRenderer() {
		super(null, true);
	}
	
	@Override
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("water_still");
		} else {
			return map.getTile("water_flow");
		}
		
	}
	
	@Override
	public Color getColorization(WorldBlock block) {
		return block.getBiome().getWaterColor();
	}
	
}
