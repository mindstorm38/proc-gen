package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityCubePart;
import fr.theorozier.procgen.common.entity.PigEntity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TexturePredefinedMap;

public class PigEntityRenderer extends MotionEntityRenderer<PigEntity> {
	
	private static final float MIN_LEG_ROTATION = (float) Math.toRadians(10.0);
	
	private final TexturePredefinedMap baseTexture = new TexturePredefinedMap("textures/entities/pig/pig.png");
	
	private final EntityCubePart body;
	private final EntityCubePart leg;
	private final EntityCubePart head;
	
	public PigEntityRenderer() {
	
		this.body = new EntityCubePart(-0.3125f, 0.375f, -0.5f, 0.3125f, 0.875f, 0.5f);
		this.addPart("body", this.body);
		
		this.leg = new EntityCubePart(-0.125f, -0.375f, -0.125f, 0.125f, 0f, 0.125f);
		this.addPart("leg", this.leg);
		
		this.head = new EntityCubePart(-0.25f, 0.5f, 0.375f, 0.25f, 1f, 0.875f);
		this.addPart("head", this.head);
		
	}
	
	@Override
	public void initTexture() {
		TextureManager.getInstance().loadTexture(this.baseTexture);
	}
	
	@Override
	public Texture getTexture(PigEntity entity) {
		// return this.baseTexture;
		return null;
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, PigEntity entity) {
		
		this.body.render();
		this.head.render();
		
		model.push().translate(0, 0.375f, 0).rotateX(alpha - 0.5f).apply();
		this.leg.render();
		model.pop();
		
	}
	
}
