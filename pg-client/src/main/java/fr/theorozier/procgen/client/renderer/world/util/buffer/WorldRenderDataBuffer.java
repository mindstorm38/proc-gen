package fr.theorozier.procgen.client.renderer.world.util.buffer;

import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.common.util.Color;

public interface WorldRenderDataBuffer {
	
	// Basic Vertex //
	
	void vertex(float x, float y, float z);
	
	// Basic Color //
	
	void color(float r, float g, float b);
	
	default void color(Color color) {
		this.color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	default void color(Color color, float f) {
		this.color(color.getRed() * f, color.getGreen() * f, color.getBlue() * f);
	}
	
	default void colorWhite() {
		this.color(1, 1, 1);
	}
	
	// Basic Texture Coords //
	
	void texcoord(float u, float v);
	
	// Faces //
	
	void face();
	
	// Faces Vertices //
	
	void faceTop(float x, float y, float z, float dx, float dz);
	void faceBottom(float x, float y, float z, float dx, float dz);
	void faceNorth(float x, float y, float z, float dy, float dz);
	void faceSouth(float x, float y, float z, float dy, float dz);
	void faceEast(float x, float y, float z, float dy, float dx);
	void faceWest(float x, float y, float z, float dy, float dx);
	void faceVertex(int vidx, float x, float y, float z);
	
	// Face Colors //
	
	void faceColor(float r, float g, float b);
	void faceColor(int vidx, float r, float g, float b);
	
	default void faceColorWhite() {
		this.faceColor(1, 1, 1);
	}
	
	default void faceColorWhite(int vidx) {
		this.faceColor(vidx, 1, 1, 1);
	}
	
	default void faceColorGray(float f) {
		this.faceColor(f, f, f);
	}
	
	default void faceColorGray(int vidx, float f) {
		this.faceColor(vidx, f, f, f);
	}
	
	default void faceColor(Color color) {
		this.faceColor(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	default void faceColor(int vidx, Color color) {
		this.faceColor(vidx, color.getRed(), color.getGreen(), color.getBlue());
	}
	
	default void faceColor(Color color, float f) {
		this.faceColor(color.getRed() * f, color.getGreen() * f, color.getBlue() * f);
	}
	
	default void faceColor(int vidx, Color color, float f) {
		this.faceColor(vidx, color.getRed() * f, color.getGreen() * f, color.getBlue() * f);
	}
	
	default void faceTopColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 8) == 8 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 4) == 4 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 2) == 2 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 1) == 1 ? occlFactor : 1f);
	}
	
	default void faceBottomColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 16) == 16 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 128) == 128 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 64) == 64 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 32) == 32 ? occlFactor : 1f);
	}
	
	default void faceNorthColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 512) == 512 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 256) == 256 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 2048) == 2048 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 1024) == 1024 ? occlFactor : 1f);
	}
	
	default void faceSouthColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 4096) == 4096 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 32768) == 32768 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 16384) == 16384 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 8192) == 8192 ? occlFactor : 1f);
	}
	
	default void faceEastColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 65536) == 65536 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 524288) == 524288 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 262144) == 262144 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 131072) == 131072 ? occlFactor : 1f);
	}
	
	default void faceWestColor(Color color, float occlFactor, int occlData) {
		this.faceColor(0, color, (occlData & 2097152) == 2097152 ? occlFactor : 1f);
		this.faceColor(1, color, (occlData & 1048576) == 1048576 ? occlFactor : 1f);
		this.faceColor(2, color, (occlData & 8388608) == 8388608 ? occlFactor : 1f);
		this.faceColor(3, color, (occlData & 4194304) == 4194304 ? occlFactor : 1f);
	}
	
	// Face Texcoords //
	
	void faceTexCoordsRot0(float u, float v, float w, float h);
	void faceTexCoordsRot1(float u, float v, float w, float h);
	void faceTexCoordsRot2(float u, float v, float w, float h);
	void faceTexCoordsRot3(float u, float v, float w, float h);
	void faceTexCoord(int vidx, float u, float v);
	
	default void faceTexcoords(float u, float v, float w, float h) {
		this.faceTexCoordsRot0(u, v, w, h);
	}
	
	default void faceTexcoords(float u, float v, float w, float h, int rotation) {
		
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
	
	default void faceTexcoords(TextureMapTile tile) {
		this.faceTexcoords(tile.x, tile.y, tile.width, tile.height);
	}
	
	default void faceTexcoords(TextureMapTile tile, int rotation) {
		this.faceTexcoords(tile.x, tile.y, tile.width, tile.height, rotation);
	}
	
	default void faceTexcoords(TextureMapTile tile, float xOffFactor, float yOffFactor, float wFactor, float hFactor) {
		this.faceTexcoords(tile.x + (tile.width * xOffFactor), tile.y + (tile.height * yOffFactor), tile.width * wFactor, tile.height * hFactor);
	}
	
	// Indices //
	
	int indices();
	void triangle(int a, int b, int c);
	void rect(int a, int b, int c, int d);
	
}
