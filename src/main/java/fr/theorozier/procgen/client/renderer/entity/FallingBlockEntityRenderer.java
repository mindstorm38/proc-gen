package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityCubePart;
import fr.theorozier.procgen.common.entity.FallingBlockEntity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureManager;

public class FallingBlockEntityRenderer extends MotionEntityRenderer<FallingBlockEntity> {
	
	private final SimpleTexture texture = new SimpleTexture("textures/blocks/bedrock.png");
	
	private final EntityCubePart part;
	
	public FallingBlockEntityRenderer() {
		
		this.part = new EntityCubePart(0, 0, 0, 1, 1, 1);
		this.addPart("block", this.part);
		
	}
	
	@Override
	public void initTexture() {
		TextureManager.getInstance().loadTexture(this.texture);
	}
	
	@Override
	public Texture getTexture(FallingBlockEntity entity) {
		// return this.texture;
		return null;
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, FallingBlockEntity entity) {
		
		this.part.render();
		
	}
	
}
