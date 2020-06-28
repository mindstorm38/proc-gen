package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

public class EntityCubePart extends EntityModelPart {
	
	private final float minX;
	private final float minY;
	private final float minZ;
	private final float maxX;
	private final float maxY;
	private final float maxZ;
	
	private final BlockFaces faces;
	private final TextureMapTile[] tiles;
	private final int[] tilesRotations;
	
	private IndicesDrawBuffer buffer;
	
	public EntityCubePart(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
		this.faces = new BlockFaces((byte) 0xFF);
		
		this.tiles = new TextureMapTile[Direction.values().length];
		this.tilesRotations = new int[Direction.values().length];
		
	}
	
	public BlockFaces getFaces() {
		return this.faces;
	}
	
	public void setFaceTile(Direction dir, TextureMapTile tile, int rotation) {
		this.tiles[dir.ordinal()] = tile;
		this.tilesRotations[dir.ordinal()] = rotation;
	}
	
	public void setFaceTile(Direction dir, TextureMapTile tile) {
		this.setFaceTile(dir, tile, 0);
	}
	
	protected void applyTexcoords(Direction direction, WorldRenderBuffer dataArray) {
		
		TextureMapTile tile = this.tiles[direction.ordinal()];
		
		if (tile != null) {
			dataArray.faceTexcoords(tile, this.tilesRotations[direction.ordinal()]);
		}
		
	}
	
	@Override
	public void init(WorldShaderManager shaderManager, WorldRenderBuffer renderBuffer) {
		
		float dx = this.maxX - this.minX;
		float dy = this.maxY - this.minY;
		float dz = this.maxZ - this.minZ;
		
		// Y DIRECTION
		
		if (this.faces.isTop()) {
			
			renderBuffer.face();
			renderBuffer.faceTop(this.minX, this.maxY, this.minZ, dx, dz);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.TOP, renderBuffer);
			
		}
		
		if (this.faces.isBottom()) {
			
			renderBuffer.face();
			renderBuffer.faceBottom(this.minX, this.minY, this.minZ, dx, dz);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.BOTTOM, renderBuffer);
			
		}
		
		// X DIRECTION
		
		if (this.faces.isNorth()) {
			
			renderBuffer.face();
			renderBuffer.faceNorth(this.maxX, this.minY, this.minZ, dy, dz);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.NORTH, renderBuffer);
			
		}
		
		if (this.faces.isSouth()) {
			
			renderBuffer.face();
			renderBuffer.faceSouth(this.minX, this.minY, this.minZ, dy, dz);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.SOUTH, renderBuffer);
			
		}
		
		// Z DIRECTION
		
		if (this.faces.isEast()) {
			
			renderBuffer.face();
			renderBuffer.faceEast(this.minX, this.minY, this.maxZ, dy, dx);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.EAST, renderBuffer);
			
		}
		
		if (this.faces.isWest()) {
			
			renderBuffer.face();
			renderBuffer.faceWest(this.minX, this.minY, this.minZ, dy, dx);
			renderBuffer.faceColorWhite();
			this.applyTexcoords(Direction.WEST, renderBuffer);
			
		}
		
		this.buffer = renderBuffer.newDrawBufferAndUpload(shaderManager);
		
	}
	
	@Override
	public void stop() {
		
		this.buffer.delete();
		this.buffer = null;
		
	}
	
	@Override
	public void render() {
		this.buffer.drawElements();
	}
	
}
