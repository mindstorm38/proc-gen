package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockGrassRenderer extends BlockRenderer {
	
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
		
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
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderBuffer dataArray) {
		
		Color color = getBlockColor(world, bx, by, bz, BlockColorResolver.GRASS_COLOR);
		TextureMapTile sideTile = this.getSideColorTile(map);
		int occlData = block.isBlockOpaque() ? computeAmbientOcclusion(world, bx, by, bz, faces) : 0;
		
		if (faces.isTop()) {
			
			dataArray.face();
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceTopColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP), posRand(x, y, z) % 4);
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.face();
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceBottomColor(Color.WHITE, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			
		}
		
		if (faces.isNorth()) {
			
			dataArray.face();
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceNorthColor(Color.WHITE, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
			
			dataArray.face();
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceNorthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(sideTile);
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.face();
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceSouthColor(Color.WHITE, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
			
			dataArray.face();
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceSouthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(sideTile);
			
		}
		
		if (faces.isEast()) {
			
			dataArray.face();
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceEastColor(Color.WHITE, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
			
			dataArray.face();
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceEastColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(sideTile);
			
		}
		
		if (faces.isWest()) {
			
			dataArray.face();
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceWestColor(Color.WHITE, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
			
			dataArray.face();
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceWestColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(sideTile);
			
		}
		
	}
	
}
