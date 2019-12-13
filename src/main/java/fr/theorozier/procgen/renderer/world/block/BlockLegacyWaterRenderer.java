package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

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
	public void colorize(BufferedFloatArray colors, WorldBlock block, int count) {
		addColor(colors, block.getBiome().getWaterColor(), count);
	}
	
}
