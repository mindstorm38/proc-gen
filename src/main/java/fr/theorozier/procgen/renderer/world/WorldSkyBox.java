package fr.theorozier.procgen.renderer.world;

import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class WorldSkyBox {
	
	public static final int SKYBOX_RADIUS = 16 * 24;
	
	private final Basic3DShaderManager shaderManager;
	private IndicesDrawBuffer drawBuffer;
	
	public WorldSkyBox(Basic3DShaderManager shaderManager) {
		this.shaderManager = shaderManager;
	}
	
	void init() {
		
		this.drawBuffer = this.shaderManager.createBasicDrawBuffer(true, false);
		
		FloatBuffer vertices = null;
		FloatBuffer colors = null;
		IntBuffer indices = null;
		
		try {
			
			vertices = MemoryUtil.memAllocFloat(24);
			colors = MemoryUtil.memAllocFloat(32);
			indices = MemoryUtil.memAllocInt(this.drawBuffer.setIndicesCount(36));
			
			vertices.put(-SKYBOX_RADIUS).put(-SKYBOX_RADIUS).put(-SKYBOX_RADIUS);
			vertices.put(+SKYBOX_RADIUS).put(-SKYBOX_RADIUS).put(-SKYBOX_RADIUS);
			vertices.put(-SKYBOX_RADIUS).put(-SKYBOX_RADIUS).put(+SKYBOX_RADIUS);
			vertices.put(+SKYBOX_RADIUS).put(-SKYBOX_RADIUS).put(+SKYBOX_RADIUS);
			
			vertices.put(-SKYBOX_RADIUS).put(+SKYBOX_RADIUS).put(-SKYBOX_RADIUS);
			vertices.put(+SKYBOX_RADIUS).put(+SKYBOX_RADIUS).put(-SKYBOX_RADIUS);
			vertices.put(-SKYBOX_RADIUS).put(+SKYBOX_RADIUS).put(+SKYBOX_RADIUS);
			vertices.put(+SKYBOX_RADIUS).put(+SKYBOX_RADIUS).put(+SKYBOX_RADIUS);
			
			for (int i = 0; i < 8; i++) {
				colors.put(0.611764705f).put(0.917647058f).put(1f).put(1f);
			}
			
			indices.put(0).put(1).put(5);
			indices.put(0).put(5).put(4);
			
			indices.put(1).put(7).put(5);
			indices.put(1).put(3).put(7);
			
			indices.put(2).put(6).put(7);
			indices.put(2).put(7).put(3);
			
			indices.put(0).put(4).put(6);
			indices.put(0).put(6).put(2);
			
			indices.put(4).put(5).put(7);
			indices.put(4).put(7).put(6);
			
			indices.put(0).put(2).put(3);
			indices.put(0).put(3).put(1);
			
			vertices.flip();
			colors.flip();
			indices.flip();
			
			this.drawBuffer.bindVao();
			this.drawBuffer.uploadVboData(BasicFormat.BASIC3D_POSITION, vertices, BufferUsage.STATIC_DRAW);
			this.drawBuffer.uploadVboData(BasicFormat.BASIC_COLOR, colors, BufferUsage.DYNAMIC_DRAW);
			this.drawBuffer.uploadIboData(indices, BufferUsage.STATIC_DRAW);
			
		} finally {
			
			BufferUtils.safeFree(vertices);
			BufferUtils.safeFree(colors);
			BufferUtils.safeFree(indices);
			
		}
		
	}
	
	void stop() {
	
		this.drawBuffer.delete();
		
	}
	
	void render() {
		this.drawBuffer.drawElements();
	}

}
