package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

public class BlockCrossRenderer extends BlockRenderer {
	
	private static final float OFFSET  = 0.1464466094f;
	private static final float SIZE    = 0.7071067812f;
	private static final float OFFSIZE = OFFSET + SIZE;
	private static final float HEIGHT  = 1f;
	
	private final String mapTileIdentifier;
	
	public BlockCrossRenderer(String mapTileIdentifier) {
		
		this.mapTileIdentifier = mapTileIdentifier;
		
	}
	
	public TextureMapTile getCrossTile(WorldBlock block, TextureMap map) {
		return map.getTile(this.mapTileIdentifier);
	}
	
	@Override
	public int getRenderData(WorldBlock block, float x, float y, float z, int idx, BlockFaces faces, TextureMap map, BufferedFloatArray vertices, BufferedFloatArray texcoords, BufferedIntArray indices) {
		
		//if (faces.isVisible()) {
		
			TextureMapTile tile = this.getCrossTile(block, map);
			
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
			
			indices.put(idx    ).put(idx + 3).put(idx + 7);
			indices.put(idx    ).put(idx + 7).put(idx + 4);
			indices.put(idx    ).put(idx + 4).put(idx + 7);
			indices.put(idx    ).put(idx + 7).put(idx + 3);
			
			indices.put(idx + 1).put(idx + 5).put(idx + 6);
			indices.put(idx + 1).put(idx + 6).put(idx + 2);
			indices.put(idx + 1).put(idx + 2).put(idx + 6);
			indices.put(idx + 1).put(idx + 6).put(idx + 5);
			
			return idx + 8;
			
		//}
		
		//return idx;
		
	}
	
}
