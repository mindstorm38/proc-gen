package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockLegacyWaterRenderer extends BlockCubeRenderer {
	
	public BlockLegacyWaterRenderer() {
		super(null, true);
	}
	
	@Override
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("water_still");
		} else {
			return map.getTile("water_flow");
		}
		
	}
	
	@Override
	public Color getColorization(WorldBase world, BlockState block, int x, int y, int z) {
		return world.getBiomeAt(x, z).getWaterColor();
	}
	
}
