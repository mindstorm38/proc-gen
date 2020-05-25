package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;

public abstract class EntityModelPart {

	private boolean initied = false;
	
	public abstract void init(WorldShaderManager shaderManager, WorldRenderDataArray dataArray);
	public abstract void stop();
	
	public abstract void render();
	
	public void initPart(WorldShaderManager shaderManager, WorldRenderDataArray dataArray) {
		
		if (this.initied)
			throw new IllegalStateException("This entity part is already initialized.");
		
		this.init(shaderManager, dataArray);
		this.initied = true;
		
	}
	
	public void stopPart() {
		
		if (!this.initied)
			throw new IllegalStateException("Can't stop this entity part cause it's not initialized.");
		
		this.stop();
		this.initied = false;
		
	}

}
