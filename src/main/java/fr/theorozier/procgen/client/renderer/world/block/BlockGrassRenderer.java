package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.util.array.BufferedFloatArray;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

import java.util.function.BiConsumer;

public class BlockGrassRenderer extends BlockRenderer {
	
	private interface TexcoordsApplier extends BiConsumer<BufferedFloatArray, TextureMapTile> {}
	
	private static final TexcoordsApplier[] TOP_TEXCOORDS_APPLIERS = {
			(texcoords, tile) -> texcoords.put(tile.x             ).put(tile.y              ),
			(texcoords, tile) -> texcoords.put(tile.x             ).put(tile.y + tile.height),
			(texcoords, tile) -> texcoords.put(tile.x + tile.width).put(tile.y + tile.height),
			(texcoords, tile) -> texcoords.put(tile.x + tile.width).put(tile.y              )
	};
	
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
		if (face == Direction.TOP) {
			return map.getTile("grass_top");
		} else if (face == Direction.BOTTOM) {
			return map.getTile("dirt");
		} else {
			return map.getTile("grass_side");
		}
		
	}
	
	public TextureMapTile getSideColorTile(TextureMap map) {
		return map.getTile("grass_side_color");
	}
	
	public Color getColorization(WorldBlock block) {
		return block.getBiome().getFoliageColor();
	}
	
	@Override
	public void getRenderData(WorldBlock block, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		Color color = this.getColorization(block);
		TextureMapTile sideTile = this.getSideColorTile(map);
		
		if (faces.isTop()) {
			
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP));
			dataArray.faceIndices();
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			dataArray.faceIndices();
			
		}
		
		if (faces.isNorth()) {
			
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
			dataArray.faceTexcoords(sideTile);
			dataArray.faceIndices();
			dataArray.faceIndices();
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
			dataArray.faceTexcoords(sideTile);
			dataArray.faceIndices();
			dataArray.faceIndices();
			
		}
		
		if (faces.isEast()) {
			
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
			dataArray.faceTexcoords(sideTile);
			dataArray.faceIndices();
			dataArray.faceIndices();
			
		}
		
		if (faces.isWest()) {
			
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
			dataArray.faceTexcoords(sideTile);
			dataArray.faceIndices();
			dataArray.faceIndices();
			
		}
		
		/*
		int offset = posRand(x, y, z);
			
			for (int i = 0; i < TOP_TEXCOORDS_APPLIERS.length; ++i)
				TOP_TEXCOORDS_APPLIERS[(i + offset) & 3].accept(texcoords, tile);
		 */
		
	}
	
}
