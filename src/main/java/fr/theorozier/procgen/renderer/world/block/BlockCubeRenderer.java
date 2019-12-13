package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.renderer.world.ColorMapManager;
import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.biome.BiomeWeatherRange;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.texture.TextureMapTile;

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
	
	@Override
	public int getRenderData(WorldBlock block, float x, float y, float z, int idx, BlockFaces faces, TextureMap map, ColorMapManager colorMap, BufferedFloatArray colors, BufferedIntArray indices, BufferedFloatArray texcoords, BufferedFloatArray vertices) {
		
		TextureMapTile tile;
		int colorsCount = 0;
		
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
			colorsCount += 4;
			
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
			colorsCount += 4;
			
		}
		
		if (faces.isNorth()) {
			
			tile = this.getFaceTile(block, map, Direction.NORTH);
			
			vertices.put(x + 1).put(y + 1).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z    );
			vertices.put(x + 1).put(y + 1).put(z    );
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			colorsCount += 4;
			
		}
		
		if (faces.isSouth()) {
			
			tile = this.getFaceTile(block, map, Direction.SOUTH);
			
			vertices.put(x).put(y + 1).put(z    );
			vertices.put(x).put(y    ).put(z    );
			vertices.put(x).put(y    ).put(z + 1);
			vertices.put(x).put(y + 1).put(z + 1);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			colorsCount += 4;
			
		}
		
		if (faces.isEast()) {
			
			tile = this.getFaceTile(block, map, Direction.EAST);
			
			vertices.put(x    ).put(y + 1).put(z + 1);
			vertices.put(x    ).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y    ).put(z + 1);
			vertices.put(x + 1).put(y + 1).put(z + 1);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			colorsCount += 4;
			
		}
		
		if (faces.isWest()) {
			
			tile = this.getFaceTile(block, map, Direction.WEST);
			
			vertices.put(x + 1).put(y + 1).put(z);
			vertices.put(x + 1).put(y    ).put(z);
			vertices.put(x    ).put(y    ).put(z);
			vertices.put(x    ).put(y + 1).put(z);
			
			texcoords.put(tile.x             ).put(tile.y              );
			texcoords.put(tile.x             ).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y + tile.height);
			texcoords.put(tile.x + tile.width).put(tile.y              );
			
			indices.put(idx).put(idx + 1).put(idx + 2);
			indices.put(idx).put(idx + 2).put(idx + 3);
			
			idx += 4;
			colorsCount += 4;
			
		}
		
		if (this.needColorization) {
			
			/*
			int finalColorsCount = colorsCount;
			
			BiomeWeatherRange biome = block.getBiome().getWeather();
			colorMap.getFoliageColor(color -> addColor(colors, color, finalColorsCount), biome.getTypicalTemp(), biome.getTypicalHumidity());
			*/
			
			addColor(colors, block.getBiome().getFoliageColor(), colorsCount);
			
		} else {
			addWhiteColor(colors, colorsCount);
		}
		
		return idx;
		
	}
	
}
