package fr.theorozier.procgen.client.renderer.buffer;

import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.util.array.BufferedFloatArray;
import fr.theorozier.procgen.common.util.array.BufferedIntArray;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * <p>Mon petit bijoux de simplification.</p>
 * <p>Allows in particular to avoid passing the 4 vertices, color, texcoords, and indices buffers.</p>
 * <p>This is also an API to these buffers, then if in the future only one buffer has to be used instead
 * of vertices, colors & texcoords buffers.</p>
 *
 * @author Theo Rozier
 *
 */
public class WorldRenderDataArray implements WorldRenderBuffer {
	
	private final BufferedFloatArray vertices = new BufferedFloatArray();
	private final BufferedFloatArray colors = new BufferedFloatArray();
	private final BufferedFloatArray texcoords = new BufferedFloatArray();
	private final BufferedIntArray indices = new BufferedIntArray();
	
	private int idx = 0;
	
	public WorldRenderDataArray() {}
	
	@Override
	public void clear() {
		
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
	
	@Override
	public void face() {
		this.rect(0, 1, 2, 3);
	}
	
	// Building methods //
	
	@Override
	public void vertex(float x, float y, float z) {
		this.vertices.put(x).put(y).put(z);
	}
	
	@Override
	public void color(float r, float g, float b) {
		this.colors.put(r).put(g).put(b);
	}
	
	@Override
	public void texcoord(float u, float v) {
		this.texcoords.put(u).put(v);
	}
	
	// Faces Vertices //
	
	// Origins of faces are always possible to reach with
	// translation of only one axis for a straight cube.
	// This is used to reduce computation
	
	@Override
	public void faceTop(float x, float y, float z, float dx, float dz) {
		
		vertices.put(x     ).put(y).put(z     );
		vertices.put(x     ).put(y).put(z + dz);
		vertices.put(x + dx).put(y).put(z + dz);
		vertices.put(x + dx).put(y).put(z     );
		
	}
	
	@Override
	public void faceBottom(float x, float y, float z, float dx, float dz) {
		
		vertices.put(x     ).put(y).put(z + dz);
		vertices.put(x     ).put(y).put(z     );
		vertices.put(x + dx).put(y).put(z     );
		vertices.put(x + dx).put(y).put(z + dz);
	
	}
	
	@Override
	public void faceNorth(float x, float y, float z, float dy, float dz) {
		
		vertices.put(x).put(y + dy).put(z + dz);
		vertices.put(x).put(y     ).put(z + dz);
		vertices.put(x).put(y     ).put(z     );
		vertices.put(x).put(y + dy).put(z     );
	
	}
	
	@Override
	public void faceSouth(float x, float y, float z, float dy, float dz) {
		
		vertices.put(x).put(y + dy).put(z     );
		vertices.put(x).put(y     ).put(z     );
		vertices.put(x).put(y     ).put(z + dz);
		vertices.put(x).put(y + dy).put(z + dz);
		
	}
	
	@Override
	public void faceEast(float x, float y, float z, float dy, float dx) {
		
		vertices.put(x     ).put(y + dy).put(z);
		vertices.put(x     ).put(y     ).put(z);
		vertices.put(x + dx).put(y     ).put(z);
		vertices.put(x + dx).put(y + dy).put(z);
		
	}
	
	@Override
	public void faceWest(float x, float y, float z, float dy, float dx) {
		
		vertices.put(x + dx).put(y + dy).put(z);
		vertices.put(x + dx).put(y     ).put(z);
		vertices.put(x     ).put(y     ).put(z);
		vertices.put(x     ).put(y + dy).put(z);
		
	}
	
	@Override
	public void faceVertex(int vidx, float x, float y, float z) {
		this.vertex(x, y, z);
	}
	
	// Face Colors //
	
	@Override
	public void faceColor(float r, float g, float b) {
		
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		colors.put(r).put(g).put(b);
		
	}
	
	@Override
	public void faceColor(int vidx, float r, float g, float b) {
		this.color(r, g, b);
	}
	
	// Face Texcoords //
	
	@Override
	public void faceTexCoordsRot0(float u, float v, float w, float h) {
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot1(float u, float v, float w, float h) {
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot2(float u, float v, float w, float h) {
		texcoords.put(u + w).put(v + h);
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
	}
	
	@Override
	public void faceTexCoordsRot3(float u, float v, float w, float h) {
		texcoords.put(u + w).put(v    );
		texcoords.put(u    ).put(v    );
		texcoords.put(u    ).put(v + h);
		texcoords.put(u + w).put(v + h);
	}
	
	@Override
	public void faceTexCoord(int vidx, float u, float v) {
		this.texcoord(u, v);
	}
	
	// Indices //
	
	@Override
	public int indices() {
		return this.getIndicesCount();
	}
	
	@Override
	public void triangle(int a, int b, int c) {
		
		this.indices.put(this.idx + a).put(this.idx + b).put(this.idx + c);
		this.idx += 3;
		
	}
	
	@Override
	public void rect(int a, int b, int c, int d) {
		
		this.indices.put(this.idx + a).put(this.idx + b).put(this.idx + c);
		this.indices.put(this.idx + a).put(this.idx + c).put(this.idx + d);
		this.idx += 4;
		
	}
	
	// Upload method //
	
	@Override
	public void upload(IndicesDrawBuffer drawBuffer) {
		
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
	
	@Override
	public IndicesDrawBuffer newDrawBuffer(WorldShaderManager shaderManager) {
		return shaderManager.createBasicDrawBuffer(true, true);
	}
	
	@Override
	public String toString() {
		return "WorldRenderDataArray{" +
				"vertices=" + vertices.getSize() +
				", colors=" + colors.getSize() +
				", texcoords=" + texcoords.getSize() +
				", indices=" + indices.getSize() +
				", idx=" + idx +
				'}';
	}
	
}
