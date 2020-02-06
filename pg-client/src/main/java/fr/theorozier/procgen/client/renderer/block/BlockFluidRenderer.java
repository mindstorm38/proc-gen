package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.BlockFluid;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockFluidRenderer extends BlockColorizableRenderer {
	
	private static final float TOP_OFFSET = 0.0625f;
	private static final float TOP_HEIGHT = 0.9375f;
	
	private final String topMapTileId;
	private final String sideMapTileId;
	private final BlockFluid fluidBlock;
	
	public BlockFluidRenderer(String topMapTileId, String sideMapTileId, boolean needColorization, BlockFluid fluidBlock) {
		
		super(needColorization);
		this.topMapTileId = topMapTileId;
		this.sideMapTileId = sideMapTileId;
		this.fluidBlock = fluidBlock;
		
	}
	
	public TextureMapTile getFaceTile(BlockState block, TextureMap map, Direction face) {
		return map.getTile(face == Direction.TOP ? this.topMapTileId : this.sideMapTileId);
	}
	
	public boolean hasSameBlockOnTop(WorldAccessor world, int x, int y, int z) {
		BlockState state = world.getBlockAt(x, y + 1, z);
		return state != null && state.isBlock(this.fluidBlock);
	}
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		Color color = this.needColorization ? getBlockColor(world, bx, by, bz, BlockColorResolver.WATER_COLOR) : Color.WHITE;
		boolean under = this.hasSameBlockOnTop(world, bx, by, bz);
		
		float height = under ? 1 : TOP_HEIGHT;
		float topOffset = under ? 0 : TOP_OFFSET;
		
		if (faces.isTop()) {
			
			dataArray.faceTop(x, y + height, z, 1, 1);
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
			
			dataArray.faceNorth(x + 1, y, z, height, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.NORTH), 0, topOffset, 1, height);
			dataArray.faceIndices();
			
		}
		
		if (faces.isSouth()) {
			
			dataArray.faceSouth(x, y, z, height, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.SOUTH), 0, topOffset, 1, height);
			dataArray.faceIndices();
			
		}
		
		if (faces.isEast()) {
			
			dataArray.faceEast(x, y, z + 1, height, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.EAST), 0, topOffset, 1, height);
			dataArray.faceIndices();
			
		}
		
		if (faces.isWest()) {
			
			dataArray.faceWest(x, y, z, height, 1);
			dataArray.faceColor(color);
			dataArray.faceTexcoords(this.getFaceTile(block, map, Direction.WEST), 0, topOffset, 1, height);
			dataArray.faceIndices();
			
		}
	
	}
	
}
