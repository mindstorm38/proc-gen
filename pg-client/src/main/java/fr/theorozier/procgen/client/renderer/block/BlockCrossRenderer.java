package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockCrossRenderer extends BlockRenderer {
	
	private static final float OFFSET  = 0.1464466094f;
	private static final float SIZE    = 0.7071067812f;
	private static final float OFFSIZE = OFFSET + SIZE;
	private static final float HEIGHT  = 1f;
	
	private final String mapTileIdentifier;
	private final BlockColorResolver colorResolver;
	
	public BlockCrossRenderer(String mapTileIdentifier, BlockColorResolver colorResolver) {
		
		this.mapTileIdentifier = mapTileIdentifier;
		this.colorResolver = colorResolver;
		
	}
	
	public TextureMapTile getCrossTile(BlockState block, TextureMap map) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	@Override
	public boolean needFaces() {
		return false;
	}
	
	@Override
	public void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderBuffer dataArray) {
		
		TextureMapTile tile = this.getCrossTile(block, map);
		
		Color color = this.colorResolver == null ? Color.WHITE : getBlockColor(world, bx, by, bz, this.colorResolver);
		
		int rand = posRand(bx, by, bz);
		x += (rand % 3) * 0.1f;
		z += ((rand >> 2) % 3) * 0.1f;
		
		dataArray.face();
		dataArray.faceVertex(0, x + OFFSET, y + HEIGHT, z + OFFSET);
		dataArray.faceVertex(1, x + OFFSET, y, z + OFFSET);
		dataArray.faceVertex(2, x + OFFSIZE, y, z + OFFSIZE);
		dataArray.faceVertex(3, x + OFFSIZE, y + HEIGHT, z + OFFSIZE);
		dataArray.faceTexcoords(tile);
		dataArray.faceColor(color);
		
		dataArray.face();
		dataArray.faceVertex(0, x + OFFSET, y + HEIGHT, z + OFFSIZE);
		dataArray.faceVertex(1, x + OFFSET, y, z + OFFSIZE);
		dataArray.faceVertex(2, x + OFFSIZE, y, z + OFFSET);
		dataArray.faceVertex(3, x + OFFSIZE, y + HEIGHT, z + OFFSET);
		dataArray.faceTexcoords(tile);
		dataArray.faceColor(color);
		
		dataArray.face();
		dataArray.faceVertex(3, x + OFFSET, y + HEIGHT, z + OFFSET);
		dataArray.faceVertex(2, x + OFFSET, y, z + OFFSET);
		dataArray.faceVertex(1, x + OFFSIZE, y, z + OFFSIZE);
		dataArray.faceVertex(0, x + OFFSIZE, y + HEIGHT, z + OFFSIZE);
		dataArray.faceTexcoords(tile);
		dataArray.faceColor(color);
		
		dataArray.face();
		dataArray.faceVertex(3, x + OFFSET, y + HEIGHT, z + OFFSIZE);
		dataArray.faceVertex(2, x + OFFSET, y, z + OFFSIZE);
		dataArray.faceVertex(1, x + OFFSIZE, y, z + OFFSET);
		dataArray.faceVertex(0, x + OFFSIZE, y + HEIGHT, z + OFFSET);
		dataArray.faceTexcoords(tile);
		dataArray.faceColor(color);
		
	}
	
}
