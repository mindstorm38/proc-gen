package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockCubeRenderer extends BlockRenderer {
	
	private final String mapTileIdentifier;
	private final boolean needColorization;
	
	public BlockCubeRenderer(String mapTileIdentifier, boolean needColorization) {
		
		this.mapTileIdentifier = mapTileIdentifier;
		this.needColorization = needColorization;
		
	}
	
	public BlockCubeRenderer(String mapTileIdentifier) {
		this(mapTileIdentifier, false);
	}
	
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	public Color getColorization(WorldBlock block) {
		return block.getBiome().getFoliageColor();
	}
	
	@Override
	public void getRenderData(WorldBlock block, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		Color color = this.needColorization ? this.getColorization(block) : Color.WHITE;
		
		if (faces.isTop()) {
			
			dataArray.faceTop(x, y + 1, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.TOP));
			dataArray.faceIndices();
			
		}
		
		if (faces.isBottom()) {
			
			dataArray.faceBottom(x, y, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.BOTTOM));
			dataArray.faceIndices();
			
		}
		
		if (faces.isNorth()) {
			
			dataArray.faceNorth(x + 1, y, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH));
			dataArray.faceIndices();
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.faceSouth(x, y, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH));
			dataArray.faceIndices();
			
		}
		
		if (faces.isEast()) {
			
			dataArray.faceEast(x, y, z + 1, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST));
			dataArray.faceIndices();
			
		}
		
		if (faces.isWest()) {
			
			dataArray.faceWest(x, y, z, 1, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST));
			dataArray.faceIndices();
			
		}
		
	}
	
}
