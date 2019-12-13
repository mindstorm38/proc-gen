package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.renderer.world.ColorMapManager;
import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public class BlockGrassRenderer extends BlockRenderer {
	
	public TextureMapTile getFaceTile(WorldBlock block, TextureMap map, Direction face) {
		
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
	public int getRenderData(WorldBlock block, float x, float y, float z, int idx, BlockFaces faces, TextureMap map, ColorMapManager colorMap, BufferedFloatArray colors, BufferedIntArray indices, BufferedFloatArray texcoords, BufferedFloatArray vertices) {
		
		TextureMapTile tile;
		
		Color grassColor = block.getBiome().getGrassColor();
		
		if (faces.isTop()) {
			
			tile = this.getFaceTile(block, map, Direction.TOP);
			
			vertices.put(x    ).put(y + 1).put(z    );
			vertices.put(x    ).put(y + 1).put(z + 1);
			vertices.put(x + 1).put(y + 1).put(z + 1);
			vertices.put(x + 1).put(y + 1).put(z    );
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			addColor(colors, grassColor, 4);
			
		}
		
		if (faces.isBottom()) {
			
			tile = this.getFaceTile(block, map, Direction.BOTTOM);
			
			vertices.put(x    ).put(y    ).put(z + 1);
			vertices.put(x    ).put(y    ).put(z    );
			vertices.put(x + 1).put(y    ).put(z    );
			vertices.put(x + 1).put(y    ).put(z + 1);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			addWhiteColor(colors, 4);
			
		}
		
		if (faces.isNorth()) {
			
			vertices.put(x + 1).put(y + 1).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z    );
			vertices.put(x + 1).put(y + 1).put(z    );
			
			vertices.put(x + 1).put(y + 1).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z    );
			vertices.put(x + 1).put(y + 1).put(z    );
			
			tile = this.getFaceTile(block, map, Direction.NORTH);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			tile = this.getSideColorTile(map);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			indices.put(idx + 4).put(idx + 5).put(idx + 6);
			indices.put(idx + 4).put(idx + 6).put(idx + 7);
			
			idx += 8;
			addWhiteColor(colors, 4);
			addColor(colors, grassColor, 4);
			
		}
		
		if (faces.isSouth()) {
			
			vertices.put(x).put(y + 1).put(z    );
			vertices.put(x).put(y    ).put(z    );
			vertices.put(x).put(y    ).put(z + 1);
			vertices.put(x).put(y + 1).put(z + 1);
			
			vertices.put(x).put(y + 1).put(z    );
			vertices.put(x).put(y    ).put(z    );
			vertices.put(x).put(y    ).put(z + 1);
			vertices.put(x).put(y + 1).put(z + 1);
			
			tile = this.getFaceTile(block, map, Direction.SOUTH);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			tile = this.getSideColorTile(map);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			indices.put(idx + 4).put(idx + 5).put(idx + 6);
			indices.put(idx + 4).put(idx + 6).put(idx + 7);
			
			idx += 8;
			addWhiteColor(colors, 4);
			addColor(colors, grassColor, 4);
			
		}
		
		if (faces.isEast()) {
			
			vertices.put(x    ).put(y + 1).put(z + 1);
			vertices.put(x    ).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y + 1).put(z + 1);
			
			vertices.put(x    ).put(y + 1).put(z + 1);
			vertices.put(x    ).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y + 1).put(z + 1);
			
			tile = this.getFaceTile(block, map, Direction.EAST);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			tile = this.getSideColorTile(map);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			indices.put(idx + 4).put(idx + 5).put(idx + 6);
			indices.put(idx + 4).put(idx + 6).put(idx + 7);
			
			idx += 8;
			addWhiteColor(colors, 4);
			addColor(colors, grassColor, 4);
			
		}
		
		if (faces.isWest()) {
			
			vertices.put(x + 1).put(y + 1).put(z);
			vertices.put(x + 1).put(y    ).put(z);
			vertices.put(x    ).put(y    ).put(z);
			vertices.put(x    ).put(y + 1).put(z);
			
			vertices.put(x + 1).put(y + 1).put(z);
			vertices.put(x + 1).put(y    ).put(z);
			vertices.put(x    ).put(y    ).put(z);
			vertices.put(x    ).put(y + 1).put(z);
			
			tile = this.getFaceTile(block, map, Direction.WEST);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			tile = this.getSideColorTile(map);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			indices.put(idx + 4).put(idx + 5).put(idx + 6);
			indices.put(idx + 4).put(idx + 6).put(idx + 7);
			
			idx += 8;
			addWhiteColor(colors, 4);
			addColor(colors, grassColor, 4);
			
		}
		
		return idx;
		
	}
	
}
