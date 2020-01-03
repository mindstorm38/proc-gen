package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.world.WorldShaderManager;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

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
		this.buffer = shaderManager.createBasicDrawBuffer(true, true);
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
