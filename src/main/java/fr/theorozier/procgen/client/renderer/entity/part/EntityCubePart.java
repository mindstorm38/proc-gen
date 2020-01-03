package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.world.WorldShaderManager;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class EntityCubePart extends EntityModelPart {
	
	private final float minX;
	private final float minY;
	private final float minZ;
	private final float maxX;
	private final float maxY;
	private final float maxZ;
	
	private IndicesDrawBuffer buffer;
	
	public EntityCubePart(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
	}
	
	@Override
	public void init(WorldShaderManager shaderManager) {
		
		this.buffer = shaderManager.createBasicDrawBuffer(true, false);
		
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
				colors.put(0.96078431f).put(0.61960784f).put(0.25882352f);
			
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
