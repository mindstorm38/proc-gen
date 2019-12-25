package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.common.util.array.BufferedFloatArray;
import fr.theorozier.procgen.common.util.array.BufferedIntArray;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import io.msengine.common.util.Color;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * Mon petit bijoux de simplification.
 *
 * @author Theo Rozier
 *
 */
public class WorldRenderDataArray {
	
	private final BufferedFloatArray vertices;
	private final BufferedFloatArray colors;
	private final BufferedFloatArray texcoords;
	private final BufferedIntArray indices;
	
	private int idx;
	
	public WorldRenderDataArray() {
		
		this.vertices = new BufferedFloatArray();
		this.colors = new BufferedFloatArray();
		this.texcoords = new BufferedFloatArray();
		this.indices = new BufferedIntArray();
		
		this.idx = 0;
		
	}
	
	public void resetBuffers() {
		
		this.vertices.setSize(0);
		this.colors.setSize(0);
		this.texcoords.setSize(0);
		this.indices.setSize(0);
		
		this.idx = 0;
		
	}
	
	public void checkOverflows() {
		
		this.vertices.checkOverflow();
		this.colors.checkOverflow();
		this.texcoords.checkOverflow();
		this.indices.checkOverflow();
		
	}
	
	public boolean isEmpty() {
		return this.indices.getSize() == 0;
	}
	
	public int getIndicesCount() {
		return this.indices.getSize();
	}
	
	public BufferedFloatArray getVertices() {
		return this.vertices;
	}
	
	public BufferedFloatArray getColors() {
		return this.colors;
	}
	
	public BufferedFloatArray getTexcoords() {
		return this.texcoords;
	}
	
	public BufferedIntArray getIndices() {
		return this.indices;
	}
	
	// Building methods //
	
	public void vertex(float x, float y, float z) {
		this.vertices.put(x).put(y).put(z);
	}
	
	public void color(float r, float g, float b) {
		this.colors.put(r).put(g).put(b);
	}
	
	public void color(Color color) {
		this.color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public void color(Color color, float f) {
		this.color(color.getRed() * f, color.getGreen() * f, color.getBlue() * f);
	}
	
	public void colorWhite() {
		this.colors.put(1).put(1).put(1);
	}
	
	public void texcoord(float u, float v) {
		this.texcoords.put(u).put(v);
	}
	
	public void triangle(int a, int b, int c) {
		
		this.indices.put(this.idx + a).put(this.idx + b).put(this.idx + c);
		this.idx += 3;
		
	}
	
	public void rect(int a, int b, int c, int d) {
		
		this.indices.put(this.idx + a).put(this.idx + b).put(this.idx + c);
		this.indices.put(this.idx + a).put(this.idx + c).put(this.idx + d);
		this.idx += 4;
		
	}
	
	// Faces Vertices //
	
	// Origins of faces are always possible to reach with
	// translation of only one axis for a straight cube.
	// This is used to reduce computation
	
	public void faceTop(float x, float y, float z, float dx, float dz) {
		
		vertices.put(x     ).put(y).put(z     );
		vertices.put(x     ).put(y).put(z + dz);
		vertices.put(x + dx).put(y).put(z + dz);
		vertices.put(x + dx).put(y).put(z     );
		
	}
	
	public void faceBottom(float x, float y, float z, float dx, float dz) {
		
		vertices.put(x     ).put(y).put(z + dz);
		vertices.put(x     ).put(y).put(z     );
		vertices.put(x + dx).put(y).put(z     );
		vertices.put(x + dx).put(y).put(z + dz);
	
	}
	
	public void faceNorth(float x, float y, float z, float dy, float dz) {
		
		vertices.put(x).put(y + dy).put(z + dz);
		vertices.put(x).put(y     ).put(z + dz);
		vertices.put(x).put(y     ).put(z     );
		vertices.put(x).put(y + dy).put(z     );
	
	}
	
	public void faceSouth(float x, float y, float z, float dy, float dz) {
		
		vertices.put(x).put(y + dy).put(z     );
		vertices.put(x).put(y     ).put(z     );
		vertices.put(x).put(y     ).put(z + dz);
		vertices.put(x).put(y + dy).put(z + dz);
		
	}
	
	public void faceEast(float x, float y, float z, float dy, float dx) {
		
		vertices.put(x     ).put(y + dy).put(z);
		vertices.put(x     ).put(y     ).put(z);
		vertices.put(x + dx).put(y     ).put(z);
		vertices.put(x + dx).put(y + dy).put(z);
		
	}
	
	public void faceWest(float x, float y, float z, float dy, float dx) {
		
		vertices.put(x + dx).put(y + dy).put(z);
		vertices.put(x + dx).put(y     ).put(z);
		vertices.put(x     ).put(y     ).put(z);
		vertices.put(x     ).put(y + dy).put(z);
		
	}
	
	// Face Colors //
	
	public void faceColor(float r, float g, float b) {
		
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		
	}
	
	public void faceColorWhite() {
		this.faceColor(1, 1, 1);
	}
	
	public void faceColorGray(float f) {
		this.faceColor(f, f, f);
	}
	
	public void faceColor(Color color) {
		this.faceColor(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public void faceColor(Color color, float f) {
		this.faceColor(color.getRed() * f, color.getGreen() * f, color.getBlue() * f);
	}
	
	public void faceTopColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 8) == 8 ? occlFactor : 1f);
		this.color(color, (occlData & 4) == 4 ? occlFactor : 1f);
		this.color(color, (occlData & 2) == 2 ? occlFactor : 1f);
		this.color(color, (occlData & 1) == 1 ? occlFactor : 1f);
	}
	
	public void faceBottomColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 16) == 16 ? occlFactor : 1f);
		this.color(color, (occlData & 128) == 128 ? occlFactor : 1f);
		this.color(color, (occlData & 64) == 64 ? occlFactor : 1f);
		this.color(color, (occlData & 32) == 32 ? occlFactor : 1f);
	}
	
	public void faceNorthColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 512) == 512 ? occlFactor : 1f);
		this.color(color, (occlData & 256) == 256 ? occlFactor : 1f);
		this.color(color, (occlData & 2048) == 2048 ? occlFactor : 1f);
		this.color(color, (occlData & 1024) == 1024 ? occlFactor : 1f);
	}
	
	public void faceSouthColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 4096) == 4096 ? occlFactor : 1f);
		this.color(color, (occlData & 32768) == 32768 ? occlFactor : 1f);
		this.color(color, (occlData & 16384) == 16384 ? occlFactor : 1f);
		this.color(color, (occlData & 8192) == 8192 ? occlFactor : 1f);
	}
	
	public void faceEastColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 65536) == 65536 ? occlFactor : 1f);
		this.color(color, (occlData & 524288) == 524288 ? occlFactor : 1f);
		this.color(color, (occlData & 262144) == 262144 ? occlFactor : 1f);
		this.color(color, (occlData & 131072) == 131072 ? occlFactor : 1f);
	}
	
	public void faceWestColor(Color color, float occlFactor, int occlData) {
		this.color(color, (occlData & 2097152) == 2097152 ? occlFactor : 1f);
		this.color(color, (occlData & 1048576) == 1048576 ? occlFactor : 1f);
		this.color(color, (occlData & 8388608) == 8388608 ? occlFactor : 1f);
		this.color(color, (occlData & 4194304) == 4194304 ? occlFactor : 1f);
	}
	
	// Face Texcoords //
	
	public void faceTexCoordsRot0(float u, float v, float w, float h) {
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
	}
	
	public void faceTexCoordsRot1(float u, float v, float w, float h) {
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
	}
	
	public void faceTexCoordsRot2(float u, float v, float w, float h) {
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
	}
	
	public void faceTexCoordsRot3(float u, float v, float w, float h) {
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
	}
	
	public void faceTexcoords(float u, float v, float w, float h) {
		this.faceTexCoordsRot0(u, v, w, h);
	}
	
	public void faceTexcoords(float u, float v, float w, float h, int rotation) {
		
		switch (rotation) {
			case 0:
				this.faceTexCoordsRot0(u, v, w, h);
				break;
			case 1:
				this.faceTexCoordsRot1(u, v, w, h);
				break;
			case 2:
				this.faceTexCoordsRot2(u, v, w, h);
				break;
			default:
				this.faceTexCoordsRot3(u, v, w, h);
				break;
		}
		
	}
	
	public void faceTexcoords(TextureMapTile tile) {
		this.faceTexcoords(tile.x, tile.y, tile.width, tile.height);
	}
	
	public void faceTexcoords(TextureMapTile tile, int rotation) {
		this.faceTexcoords(tile.x, tile.y, tile.width, tile.height, rotation);
	}
	
	// Face Indices //
	
	public void faceIndices() {
		this.rect(0, 1, 2, 3);
	}
	
	// Upload method //
	
	public void uploadToDrawBuffer(IndicesDrawBuffer drawBuffer) {
		
		FloatBuffer verticesBuf = null;
		FloatBuffer colorsBuf = null;
		FloatBuffer texcoordsBuf = null;
		IntBuffer indicesBuf = null;
		
		try {
			
			verticesBuf = MemoryUtil.memAllocFloat(this.vertices.getSize());
			colorsBuf = MemoryUtil.memAllocFloat(this.colors.getSize());
			texcoordsBuf = MemoryUtil.memAllocFloat(this.texcoords.getSize());
			indicesBuf = MemoryUtil.memAllocInt(drawBuffer.setIndicesCount(this.indices.getSize()));
			
			this.vertices.resultToBuffer(verticesBuf);
			this.colors.resultToBuffer(colorsBuf);
			this.texcoords.resultToBuffer(texcoordsBuf);
			this.indices.resultToBuffer(indicesBuf);
			
			verticesBuf.flip();
			colorsBuf.flip();
			texcoordsBuf.flip();
			indicesBuf.flip();
			
			drawBuffer.bindVao();
			drawBuffer.uploadVboData(BasicFormat.BASIC3D_POSITION, verticesBuf, BufferUsage.DYNAMIC_DRAW);
			drawBuffer.uploadVboData(BasicFormat.BASIC_COLOR, colorsBuf, BufferUsage.DYNAMIC_DRAW);
			drawBuffer.uploadVboData(BasicFormat.BASIC_TEX_COORD, texcoordsBuf, BufferUsage.DYNAMIC_DRAW);
			drawBuffer.uploadIboData(indicesBuf, BufferUsage.DYNAMIC_DRAW);
			
		} finally {
			
			BufferUtils.safeFree(verticesBuf);
			BufferUtils.safeFree(colorsBuf);
			BufferUtils.safeFree(texcoordsBuf);
			BufferUtils.safeFree(indicesBuf);
			
		}
	
	}
	
}
