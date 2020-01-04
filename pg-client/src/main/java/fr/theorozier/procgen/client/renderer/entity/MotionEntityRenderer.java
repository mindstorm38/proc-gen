package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.common.entity.MotionEntity;
import io.msengine.client.renderer.model.ModelHandler;

public abstract class MotionEntityRenderer<E extends MotionEntity> extends EntityRenderer<E> {
	
	@Override
	public void renderEntity(float alpha, ModelHandler model, E entity) {
		
		model.push().translate((float) entity.getLerpedX(alpha), (float) entity.getLerpedY(alpha), (float) entity.getLerpedZ(alpha)).apply();
		this.renderMotionEntity(alpha, model, entity);
		model.pop();
		
	}
	
	public abstract void renderMotionEntity(float alpha, ModelHandler model, E entity);
	
}
