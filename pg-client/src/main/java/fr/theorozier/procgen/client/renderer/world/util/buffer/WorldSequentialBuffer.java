package fr.theorozier.procgen.client.renderer.world.util.buffer;

import fr.theorozier.procgen.client.renderer.util.MemoryGrowingBuffer;
import fr.theorozier.procgen.common.util.GrowingBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class WorldSequentialBuffer implements WorldRenderDataBuffer {
	
	private static final float[] FACE_BUFFER = new float[32];
	
	private final GrowingBuffer<FloatBuffer> data = MemoryGrowingBuffer.newFloatBuffer();
	private final GrowingBuffer<IntBuffer> indices = MemoryGrowingBuffer.newIntBuffer();
	private int idx = 0;
	
	public WorldSequentialBuffer() {
		this.data.alloc();
		this.indices.alloc();
	}
	
	public FloatBuffer getData() {
		return this.data.get();
	}
	
	public IntBuffer getIndices() {
		return this.indices.get();
	}
	
	public void free() {
		this.data.free();
		this.indices.free();
	}
	
	public void clear() {
		this.data.clear();
		this.indices.clear();
	}
	
	public void flip() {
		this.data.flip();
		this.indices.flip();
	}
	
	// Vertices count //
	
	@Override
	public int vertices() {
		return this.idx;
	}
	
	// Basic Vertex //
	
	@Override
	public void vertex(float x, float y, float z) {
		this.data.ensure(3).put(x).put(y).put(z);
	}
	
	// Basic Color //
	
	@Override
	public void color(float r, float g, float b) {
		this.data.ensure(3).put(r).put(g).put(b);
	}
	
	// Basic Texture Coords //
	
	@Override
	public void texcoord(float u, float v) {
		this.data.ensure(2).put(u).put(v);
	}
	
	// Faces //
	
	@Override
	public void face() {
		this.data.ensure(32).put(FACE_BUFFER);
		this.rect(0, 1, 2, 3);
	}
	
	// Faces Vertices //
	
	// Origins of faces are always possible to reach with
	// translation of only one axis for a straight cube.
	// This is used to reduce computation
	
	@Override
	public void faceTop(float x, float y, float z, float dx, float dz) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y).put(z     );
		buf.position(pos - 24).put(x     ).put(y).put(z + dz);
		buf.position(pos - 16).put(x + dx).put(y).put(z + dz);
		buf.position(pos - 8 ).put(x + dx).put(y).put(z     );
		buf.position(pos);
	}
	
	@Override
	public void faceBottom(float x, float y, float z, float dx, float dz) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y).put(z + dz);
		buf.position(pos - 24).put(x     ).put(y).put(z     );
		buf.position(pos - 16).put(x + dx).put(y).put(z     );
		buf.position(pos - 8 ).put(x + dx).put(y).put(z + dz);
		buf.position(pos);
	}
	
	@Override
	public void faceNorth(float x, float y, float z, float dy, float dz) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x).put(y + dy).put(z + dz);
		buf.position(pos - 24).put(x).put(y     ).put(z + dz);
		buf.position(pos - 16).put(x).put(y     ).put(z     );
		buf.position(pos - 8 ).put(x).put(y + dy).put(z     );
		buf.position(pos);
	}
	
	@Override
	public void faceSouth(float x, float y, float z, float dy, float dz) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x).put(y + dy).put(z     );
		buf.position(pos - 24).put(x).put(y     ).put(z     );
		buf.position(pos - 16).put(x).put(y     ).put(z + dz);
		buf.position(pos - 8 ).put(x).put(y + dy).put(z + dz);
		buf.position(pos);
	}
	
	@Override
	public void faceEast(float x, float y, float z, float dy, float dx) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x     ).put(y + dy).put(z);
		buf.position(pos - 24).put(x     ).put(y     ).put(z);
		buf.position(pos - 16).put(x + dx).put(y     ).put(z);
		buf.position(pos - 8 ).put(x + dx).put(y + dy).put(z);
		buf.position(pos);
	}
	
	@Override
	public void faceWest(float x, float y, float z, float dy, float dx) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32).put(x + dx).put(y + dy).put(z);
		buf.position(pos - 24).put(x + dx).put(y     ).put(z);
		buf.position(pos - 16).put(x     ).put(y     ).put(z);
		buf.position(pos - 8 ).put(x     ).put(y + dy).put(z);
		buf.position(pos);
	}
	
	@Override
	public void faceVertex(int vidx, float x, float y, float z) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 32 + (vidx << 3)).put(x).put(y).put(z);
		buf.position(pos);
	}
	
	// Face Colors //
	
	@Override
	public void faceColor(float r, float g, float b) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 29).put(r).put(g).put(b);
		buf.position(pos - 21).put(r).put(g).put(b);
		buf.position(pos - 13).put(r).put(g).put(b);
		buf.position(pos - 5 ).put(r).put(g).put(b);
		buf.position(pos);
	}
	
	@Override
	public void faceColor(int vidx, float r, float g, float b) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 29 + (vidx << 3)).put(r).put(g).put(b);
		buf.position(pos);
	}
	
	// Face Texcoords //
	
	@Override
	public void faceTexCoordsRot0(float u, float v, float w, float h) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 26).put(u    ).put(v    );
		buf.position(pos - 18).put(u    ).put(v + h);
		buf.position(pos - 10).put(u + w).put(v + h);
		buf.position(pos - 2 ).put(u + w).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot1(float u, float v, float w, float h) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 26).put(u    ).put(v + h);
		buf.position(pos - 18).put(u + w).put(v + h);
		buf.position(pos - 10).put(u + w).put(v    );
		buf.position(pos - 2 ).put(u    ).put(v    );
	}
	
	@Override
	public void faceTexCoordsRot2(float u, float v, float w, float h) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 26).put(u + w).put(v + h);
		buf.position(pos - 18).put(u + w).put(v    );
		buf.position(pos - 10).put(u    ).put(v    );
		buf.position(pos - 2 ).put(u    ).put(v + h);
	}
	
	@Override
	public void faceTexCoordsRot3(float u, float v, float w, float h) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 26).put(u + w).put(v    );
		buf.position(pos - 18).put(u    ).put(v    );
		buf.position(pos - 10).put(u    ).put(v + h);
		buf.position(pos - 2 ).put(u + w).put(v + h);
	}
	
	@Override
	public void faceTexCoord(int vidx, float u, float v) {
		FloatBuffer buf = this.data.get();
		int pos = buf.position();
		buf.position(pos - 26 + (vidx << 3)).put(u).put(v);
		buf.position(pos);
	}
	
	// Indices //
	
	@Override
	public void triangle(int a, int b, int c) {
		int i = this.idx;
		this.indices.ensure(3).put(i + a).put(i + b).put(i + c);
		this.idx = i + 3;
	}
	
	@Override
	public void rect(int a, int b, int c, int d) {
		int i = this.idx;
		this.indices.ensure(6)
				.put(i + a).put(i + b).put(i + c)
				.put(i + a).put(i + c).put(i + d);
		this.idx = i + 4;
	}
	
}
