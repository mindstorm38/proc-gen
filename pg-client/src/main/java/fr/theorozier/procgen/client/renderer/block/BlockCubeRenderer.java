package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldRenderDataBuffer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockCubeRenderer extends BlockColorizableRenderer {
	
	private final String mapTileIdentifier;
	
	public BlockCubeRenderer(String mapTileIdentifier, boolean needColorization) {
		
		super(needColorization);
		this.mapTileIdentifier = mapTileIdentifier;
		
	}
	
	public BlockCubeRenderer(String mapTileIdentifier) {
		this(mapTileIdentifier, false);
	}
	
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataBuffer dataArray) {
		
		Color color = this.needColorization ? getBlockColor(world, bx, by, bz, BlockColorResolver.FOLIAGE_COLOR) : Color.WHITE;
		int occlData = block.isBlockOpaque() ? computeAmbientOcclusion(world, bx, by, bz, faces) : 0;
		
		if (faces.isTop()) {
			
			dataArray.face();
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceTopColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP));
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.face();
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceBottomColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			
		}
		
		if (faces.isNorth()) {
			
			dataArray.face();
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceNorthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.face();
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceSouthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
			
		}
		
		if (faces.isEast()) {
			
			dataArray.face();
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceEastColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
			
		}
		
		if (faces.isWest()) {
			
			dataArray.face();
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceWestColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
			
		}
		
	}
	
}
