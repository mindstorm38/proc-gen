package fr.theorozier.procgen.client.renderer.buffer;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class WorldRenderSequentialBuffer implements WorldRenderBuffer {
	
	private static final float[] FACE_BUFFER = new float[32];
	
	private FloatBuffer data = null;
	private IntBuffer indices = null;
	private int idx = 0;
	
	public FloatBuffer getData() {
		return this.data;
	}
	
	public IntBuffer getIndices() {
		return this.indices;
	}
	
	public void allocRaw(int dataCapacity, int indicesCapacity) {
		this.free();
		this.data = MemoryUtil.memAllocFloat(dataCapacity);
		this.indices = MemoryUtil.memAllocInt(indicesCapacity);
	}
	
	public void allocVertices(int verticesCapacity, int indicesCapacity) {
		this.allocRaw(verticesCapacity * 8, indicesCapacity);
	}
	
	public void allocFaces(int facesCapacity) {
		this.allocVertices(facesCapacity * 4, facesCapacity * 6);
	}
	
	public void allocBlocks(int wholeBlocksCapacity) {
		this.allocFaces(wholeBlocksCapacity * 6);
	}
	
	public void free() {
		
		if (this.data != null) {
			MemoryUtil.memFree(this.data);
			this.data = null;
		}
		
		if (this.indices != null) {
			MemoryUtil.memFree(this.indices);
			this.indices = null;
		}
		
	}
	
	public void clear() {
		this.data.clear();
		this.indices.clear();
		this.idx = 0;
	}
	
	public void flip() {
		this.data.flip();
		this.indices.flip();
	}
	
	// Basic Vertex //
	
	@Override
	public void vertex(float x, float y, float z) {
		checkDataRemaining(this.data, 3);
		this.data.put(x).put(y).put(z);
	}
	
	// Basic Color //
	
	@Override
	public void color(float r, float g, float b) {
		checkDataRemaining(this.data, 3);
		this.data.put(r).put(g).put(b);
	}
	
	// Basic Texture Coords //
	
	@Override
	public void texcoord(float u, float v) {
		checkDataRemaining(this.data, 2);
		this.data.put(u).put(v);
	}
	
	// Faces //
	
	@Override
	public void face() {
		checkDataRemaining(this.data, 32);
		this.data.put(FACE_BUFFER);
		this.rect(0, 1, 2, 3);
	}
	
	// Faces Vertices //
	
	// Origins of faces are always possible to reach with
	// translation of only one axis for a straight cube.
	// This is used to reduce computation
	
	@Override
	public void faceTop(float x, float y, float z, float dx, float dz) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y).put(z     );
		buf.position(pos - 24).put(x     ).put(y).put(z + dz);
		buf.position(pos - 16).put(x + dx).put(y).put(z + dz);
		buf.position(pos - 8 ).put(x + dx).put(y).put(z     );
		buf.position(pos);
	}
	
	@Override
	public void faceBottom(float x, float y, float z, float dx, float dz) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y).put(z + dz);
		buf.position(pos - 24).put(x     ).put(y).put(z     );
		buf.position(pos - 16).put(x + dx).put(y).put(z     );
		buf.position(pos - 8 ).put(x + dx).put(y).put(z + dz);
		buf.position(pos);
	}
	
	@Override
	public void faceNorth(float x, float y, float z, float dy, float dz) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x).put(y + dy).put(z + dz);
		buf.position(pos - 24).put(x).put(y     ).put(z + dz);
		buf.position(pos - 16).put(x).put(y     ).put(z     );
		buf.position(pos - 8 ).put(x).put(y + dy).put(z     );
		buf.position(pos);
	}
	
	@Override
	public void faceSouth(float x, float y, float z, float dy, float dz) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x).put(y + dy).put(z     );
		buf.position(pos - 24).put(x).put(y     ).put(z     );
		buf.position(pos - 16).put(x).put(y     ).put(z + dz);
		buf.position(pos - 8 ).put(x).put(y + dy).put(z + dz);
		buf.position(pos);
	}
	
	@Override
	public void faceEast(float x, float y, float z, float dy, float dx) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y + dy).put(z);
		buf.position(pos - 24).put(x     ).put(y     ).put(z);
		buf.position(pos - 16).put(x + dx).put(y     ).put(z);
		buf.position(pos - 8 ).put(x + dx).put(y + dy).put(z);
		buf.position(pos);
	}
	
	@Override
	public void faceWest(float x, float y, float z, float dy, float dx) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 32).put(x + dx).put(y + dy).put(z);
		buf.position(pos - 24).put(x + dx).put(y     ).put(z);
		buf.position(pos - 16).put(x     ).put(y     ).put(z);
		buf.position(pos - 8 ).put(x     ).put(y + dy).put(z);
		buf.position(pos);
	}
	
	@Override
	public void faceVertex(int vidx, float x, float y, float z) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 3);
		int pos = buf.position();
		vidx <<= 3;
		buf.put(pos - 32 + vidx, x);
		buf.put(pos - 31 + vidx, y);
		buf.put(pos - 30 + vidx, z);
	}
	
	// Face Colors //
	
	@Override
	public void faceColor(float r, float g, float b) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 12);
		int pos = buf.position();
		buf.position(pos - 29).put(r).put(g).put(b);
		buf.position(pos - 21).put(r).put(g).put(b);
		buf.position(pos - 13).put(r).put(g).put(b);
		buf.position(pos - 5 ).put(r).put(g).put(b);
		buf.position(pos);
	}
	
	@Override
	public void faceColor(int vidx, float r, float g, float b) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 3);
		int pos = buf.position();
		vidx <<= 3;
		buf.put(pos - 29 + vidx, r);
		buf.put(pos - 28 + vidx, g);
		buf.put(pos - 27 + vidx, b);
	}
	
	// Face Texcoords //
	
	@Override
	public void faceTexCoordsRot0(float u, float v, float w, float h) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 8);
		int pos = buf.position();
		buf.position(pos - 26).put(u    ).put(v    );
		buf.position(pos - 18).put(u    ).put(v + h);
		buf.position(pos - 10).put(u + w).put(v + h);
		buf.position(pos - 2 ).put(u + w).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot1(float u, float v, float w, float h) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 8);
		int pos = buf.position();
		buf.position(pos - 26).put(u    ).put(v + h);
		buf.position(pos - 18).put(u + w).put(v + h);
		buf.position(pos - 10).put(u + w).put(v    );
		buf.position(pos - 2 ).put(u    ).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot2(float u, float v, float w, float h) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 8);
		int pos = buf.position();
		buf.position(pos - 26).put(u + w).put(v + h);
		buf.position(pos - 18).put(u + w).put(v    );
		buf.position(pos - 10).put(u    ).put(v    );
		buf.position(pos - 2 ).put(u    ).put(v + h);
	}
	
	@Override
	public void faceTexCoordsRot3(float u, float v, float w, float h) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 8);
		int pos = buf.position();
		buf.position(pos - 26).put(u + w).put(v    );
		buf.position(pos - 18).put(u    ).put(v    );
		buf.position(pos - 10).put(u    ).put(v + h);
		buf.position(pos - 2 ).put(u + w).put(v + h);
	}
	
	@Override
	public void faceTexCoord(int vidx, float u, float v) {
		FloatBuffer buf = this.data;
		checkDataRemaining(buf, 2);
		int pos = buf.position();
		vidx <<= 3;
		buf.put(pos - 26 + vidx, u);
		buf.put(pos - 27 + vidx, u);
	}
	
	// Indices //
	
	@Override
	public int indices() {
		// FIXME: Currently not used
		return this.indices.position();
		// return this.indices.get().remaining();
	}
	
	@Override
	public void triangle(int a, int b, int c) {
		checkDataRemaining(this.indices, 3);
		int i = this.idx;
		this.indices.put(i + a).put(i + b).put(i + c);
		this.idx = i + 3;
	}
	
	@Override
	public void rect(int a, int b, int c, int d) {
		checkDataRemaining(this.indices, 6);
		int i = this.idx;
		this.indices
				.put(i + a).put(i + b).put(i + c)
				.put(i + a).put(i + c).put(i + d);
		this.idx = i + 4;
	}
	
	public int getTotalBytes() {
		return ((this.data != null ? this.data.capacity() : 0) + (this.indices != null ? this.indices.capacity() : 0)) << 2;
	}
	
	private static void checkDataRemaining(Buffer buffer, int needed) throws WorldRenderBufferOverflowException {
		int missing = needed - buffer.remaining();
		if (missing > 0) {
			throw new WorldRenderBufferOverflowException(buffer.remaining(), missing);
		}
	}
	
}
