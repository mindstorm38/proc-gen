package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
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
	public Color getColorization(WorldBase world, BlockState block, int x, int y, int z) {
		return world.getBiomeAt(x, z).getFoliageColor();
	}
	
	@Override
	public void getRenderData(WorldBase world, BlockState block, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		int ix = (int) x;
		int iy = (int) y;
		int iz = (int) z;
		
		Color color = this.needColorization ? this.getColorization(world, block, ix, iy, iz) : Color.WHITE;
		int occlData = block.isBlockOpaque() ? computeAmbientOcclusion(world, ix, iy, iz, faces) : 0;
		
		if (faces.isTop()) {
			
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceTopColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP));
			dataArray.faceIndices();
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceBottomColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			dataArray.faceIndices();
			
		}
		
		if (faces.isNorth()) {
			
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceNorthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
			dataArray.faceIndices();
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceSouthColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
			dataArray.faceIndices();
			
		}
		
		if (faces.isEast()) {
			
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceEastColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
			dataArray.faceIndices();
			
		}
		
		if (faces.isWest()) {
			
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceWestColor(color, OCCLUSION_FACTOR, occlData);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
			dataArray.faceIndices();
			
		}
		
	}
	
}
