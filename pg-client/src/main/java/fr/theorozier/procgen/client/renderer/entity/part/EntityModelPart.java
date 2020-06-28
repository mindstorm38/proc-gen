package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderSequentialBuffer;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

public abstract class EntityModelPart {

	private boolean initied = false;
	private IndicesDrawBuffer buffer = null;
	
	public abstract void draw(WorldShaderManager shaderManager, WorldRenderBuffer renderBuffer);
	
	public void stop() { }
	
	public void render() {
		this.buffer.drawElements();
	}
	
	public final void initPart(WorldShaderManager shaderManager, WorldRenderSequentialBuffer renderBuffer) {
		
		if (this.initied)
			throw new IllegalStateException("This entity part is already initialized.");
		
		this.draw(shaderManager, renderBuffer);

		this.initied = true;
		
		this.buffer = shaderManager.createSequentialDrawBuffer();
		renderBuffer.upload(this.buffer);
		
	}
	
	public final void stopPart() {
		
		if (!this.initied)
			throw new IllegalStateException("Can't stop this entity part cause it's not initialized.");
		
		this.stop();
		
		this.buffer.delete();
		this.buffer = null;
		
		this.initied = false;
		
	}

}
