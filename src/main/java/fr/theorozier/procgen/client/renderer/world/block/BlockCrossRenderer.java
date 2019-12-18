package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockCrossRenderer extends BlockRenderer {
	
	private static final float OFFSET  = 0.1464466094f;
	private static final float SIZE    = 0.7071067812f;
	private static final float OFFSIZE = OFFSET + SIZE;
	private static final float HEIGHT  = 1f;
	
	private final String mapTileIdentifier;
	private final boolean needColorization;
	
	public BlockCrossRenderer(String mapTileIdentifier, boolean needColorization) {
		
		this.mapTileIdentifier = mapTileIdentifier;
		this.needColorization = needColorization;
		
	}
	
	public TextureMapTile getCrossTile(WorldBlock block, TextureMap map) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	public Color getColorization(WorldBlock block) {
		return block.getBiome().getFoliageColor();
	}
	
	@Override
	public void getRenderData(WorldBlock block, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderDataArray dataArray) {
		
		TextureMapTile tile = this.getCrossTile(block, map);
		
		Color color = this.needColorization ? this.getColorization(block) : Color.WHITE;
		
		int rand = posRand(x, y, z);
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
		
		/*
		vertices.put(x + OFFSET ).put(y).put(z + OFFSET );
		vertices.put(x + OFFSIZE).put(y).put(z + OFFSET );
		vertices.put(x + OFFSET ).put(y).put(z + OFFSIZE);
		vertices.put(x + OFFSIZE).put(y).put(z + OFFSIZE);
		
		vertices.put(x + OFFSET ).put(y + HEIGHT).put(z + OFFSET );
		vertices.put(x + OFFSIZE).put(y + HEIGHT).put(z + OFFSET );
		vertices.put(x + OFFSET ).put(y + HEIGHT).put(z + OFFSIZE);
		vertices.put(x + OFFSIZE).put(y + HEIGHT).put(z + OFFSIZE);
		
		texcoords.put(tile.x             ).put(tile.y + tile.height);
		texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
		texcoords.put(tile.x             ).put(tile.y + tile.height);
		texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
		
		texcoords.put(tile.x             ).put(tile.y);
		texcoords.put(tile.x + tile.width).put(tile.y);
		texcoords.put(tile.x             ).put(tile.y);
		texcoords.put(tile.x + tile.width).put(tile.y);
	
		if (this.needFoliageColorization) {
			addColor(colors, block.getBiome().getFoliageColor(), 8);
		} else {
			addWhiteColor(colors, 8);
		}
		
		indices.put(idx    ).put(idx + 3).put(idx + 7);
		indices.put(idx    ).put(idx + 7).put(idx + 4);
		indices.put(idx    ).put(idx + 4).put(idx + 7);
		indices.put(idx    ).put(idx + 7).put(idx + 3);
		
		indices.put(idx + 1).put(idx + 5).put(idx + 6);
		indices.put(idx + 1).put(idx + 6).put(idx + 2);
		indices.put(idx + 1).put(idx + 2).put(idx + 6);
		indices.put(idx + 1).put(idx + 6).put(idx + 5);
		
		return idx + 8;
		*/
		
	}
	
}
