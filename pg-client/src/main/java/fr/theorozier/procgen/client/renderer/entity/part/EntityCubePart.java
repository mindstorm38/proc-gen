package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderDataArray;
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
	
	protected void applyTexcoords(Direction direction, WorldRenderDataArray dataArray) {
		
		TextureMapTile tile = this.tiles[direction.ordinal()];
		
		if (tile != null) {
			dataArray.faceTexcoords(tile, this.tilesRotations[direction.ordinal()]);
		}
		
	}
	
	@Override
	public void init(WorldShaderManager shaderManager, WorldRenderDataArray dataArray) {
		
		float dx = this.maxX - this.minX;
		float dy = this.maxY - this.minY;
		float dz = this.maxZ - this.minZ;
		
		// Y DIRECTION
		
		if (this.faces.isTop()) {
			
			dataArray.face();
			dataArray.faceTop(this.minX, this.maxY, this.minZ, dx, dz);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.TOP, dataArray);
			
		}
		
		if (this.faces.isBottom()) {
			
			dataArray.face();
			dataArray.faceBottom(this.minX, this.minY, this.minZ, dx, dz);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.BOTTOM, dataArray);
			
		}
		
		// X DIRECTION
		
		if (this.faces.isNorth()) {
			
			dataArray.face();
			dataArray.faceNorth(this.maxX, this.minY, this.minZ, dy, dz);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.NORTH, dataArray);
			
		}
		
		if (this.faces.isSouth()) {
			
			dataArray.face();
			dataArray.faceSouth(this.minX, this.minY, this.minZ, dy, dz);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.SOUTH, dataArray);
			
		}
		
		// Z DIRECTION
		
		if (this.faces.isEast()) {
			
			dataArray.face();
			dataArray.faceEast(this.minX, this.minY, this.maxZ, dy, dx);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.EAST, dataArray);
			
		}
		
		if (this.faces.isWest()) {
			
			dataArray.face();
			dataArray.faceWest(this.minX, this.minY, this.minZ, dy, dx);
			dataArray.faceColorWhite();
			this.applyTexcoords(Direction.WEST, dataArray);
			
		}
		
		this.buffer = shaderManager.createBasicDrawBuffer(true, true);
		dataArray.uploadToDrawBuffer(this.buffer);
		
		/*
		FloatBuffer vertices = null;
		FloatBuffer colors = null;
		IntBuffer indices = null;
		
		try {
			
			vertices = MemoryUtil.memAllocFloat(24);
			colors = MemoryUtil.memAllocFloat(24);
			indices = MemoryUtil.memAllocInt(this.buffer.setIndicesCount(36));
			
			vertices.put(minX).put(minY).put(minZ);
			vertices.put(maxX).put(minY).put(minZ);
			vertices.put(minX).put(minY).put(maxZ);
			vertices.put(maxX).put(minY).put(maxZ);
			vertices.put(minX).put(maxY).put(minZ);
			vertices.put(maxX).put(maxY).put(minZ);
			vertices.put(minX).put(maxY).put(maxZ);
			vertices.put(maxX).put(maxY).put(maxZ);
			
			for (int i = 0; i < 8; ++i)
				colors.put(0.96078431f + (float) Math.random()).put(0.61960784f + (float) Math.random()).put(0.25882352f + (float) Math.random());
			
			float[] texCoords = new float[4];
			
			
			// WEST
			indices.put(0).put(4).put(1);
			indices.put(1).put(4).put(5);
			
			// SOUTH
			indices.put(0).put(2).put(6);
			indices.put(0).put(6).put(4);
			
			// BOTTOM
			indices.put(0).put(1).put(3);
			indices.put(0).put(3).put(2);
			
			// EAST
			indices.put(2).put(3).put(7);
			indices.put(2).put(7).put(6);
			
			// NORTH
			indices.put(1).put(5).put(7);
			indices.put(1).put(7).put(3);
			
			// TOP
			indices.put(4).put(6).put(7);
			indices.put(4).put(7).put(5);
			
			vertices.flip();
			colors.flip();
			indices.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboData(BasicFormat.BASIC3D_POSITION, vertices, BufferUsage.STATIC_DRAW);
			this.buffer.uploadVboData(BasicFormat.BASIC_COLOR, colors, BufferUsage.STATIC_DRAW);
			this.buffer.uploadIboData(indices, BufferUsage.STATIC_DRAW);
			
		} finally {
			
			BufferUtils.safeFree(vertices);
			BufferUtils.safeFree(colors);
			BufferUtils.safeFree(indices);
			
		}
		*/
		
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
