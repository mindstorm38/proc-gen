package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockCrossRenderer extends BlockColorizableRenderer {
	
	private static final float OFFSET  = 0.1464466094f;
	private static final float SIZE    = 0.7071067812f;
	private static final float OFFSIZE = OFFSET + SIZE;
	private static final float HEIGHT  = 1f;
	
	private final String mapTileIdentifier;
	
	public BlockCrossRenderer(String mapTileIdentifier, boolean needColorization) {
		
		super(needColorization);
		this.mapTileIdentifier = mapTileIdentifier;
		
	}
	
	public TextureMapTile getCrossTile(BlockState block, TextureMap map) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	@Override
	public boolean needFaces() {
		return false;
	}
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		TextureMapTile tile = this.getCrossTile(block, map);
		
		Color color = this.needColorization ? getBlockColor(world, bx, by, bz, BlockColorResolver.FOLIAGE_COLOR) : Color.WHITE;
		
		int rand = posRand(bx, by, bz);
		x += (rand % 3) * 0.1f;
		z += ((rand >> 2) % 3) * 0.1f;
		
		dataArray.vertex(x + OFFSET, y + HEIGHT, z + OFFSET);
		dataArray.vertex(x + OFFSET, y, z + OFFSET);
		dataArray.vertex(x + OFFSIZE, y, z + OFFSIZE);
		dataArray.vertex(x + OFFSIZE, y + HEIGHT, z + OFFSIZE);
		
		dataArray.vertex(x + OFFSET, y + HEIGHT, z + OFFSIZE);
		dataArray.vertex(x + OFFSET, y, z + OFFSIZE);
		dataArray.vertex(x + OFFSIZE, y, z + OFFSET);
		dataArray.vertex(x + OFFSIZE, y + HEIGHT, z + OFFSET);
		
		for (int i = 0; i < 8; ++i)
			dataArray.color(color);
		
		dataArray.faceTexcoords(tile);
		dataArray.faceTexcoords(tile);
		
		dataArray.faceIndices();
		dataArray.faceIndices();
		
	}
	
}
