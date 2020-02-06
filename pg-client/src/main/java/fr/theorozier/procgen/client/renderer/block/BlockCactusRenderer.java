package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockCactusRenderer extends BlockRenderer {
	
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
	
		if (face == Direction.TOP) {
			return map.getTile("cactus_top");
		} else if (face == Direction.BOTTOM) {
			return map.getTile("cactus_bottom");
		} else {
			return map.getTile("cactus_side");
		}
	
	}
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		if (faces.isTop()) {
			
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP));
			dataArray.faceIndices();
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceColorWhite();
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			dataArray.faceIndices();
			
		}
		
		dataArray.faceNorth(x + 0.9375f, y, z, 1, 1);
		dataArray.faceColorWhite();
		dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
		dataArray.faceIndices();
		
		dataArray.faceSouth(x + 0.0625f, y, z, 1, 1);
		dataArray.faceColorWhite();
		dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
		dataArray.faceIndices();
		
		dataArray.faceEast(x, y, z + 0.9375f, 1, 1);
		dataArray.faceColorWhite();
		dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
		dataArray.faceIndices();
			
		dataArray.faceWest(x, y, z + 0.0625f, 1, 1);
		dataArray.faceColorWhite();
		dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
		dataArray.faceIndices();
			
	}
	
}
