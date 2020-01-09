package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityModelPart;
import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.WorldShaderManager;
import fr.theorozier.procgen.common.entity.Entity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;

import java.util.HashMap;

/**
 *
 * <u>Note : Not thread-safe.</u>
 *
 * @author Theo Rozier
 *
 * @param <E> Type of the entity to render.
 *
 */
public abstract class EntityRenderer<E extends Entity> {

	private boolean initied = false;
	
	private final HashMap<String, EntityModelPart> parts = new HashMap<>();
	
	public boolean isInitied() {
		return this.initied;
	}
	
	public void initRenderer(WorldShaderManager shaderManager, WorldRenderDataArray dataArray) {
	
		if (this.initied)
			throw new IllegalStateException("This '" + this.getClass() + "' can't be initialized twice.");
		
		this.parts.values().forEach(p -> {
			
			dataArray.resetBuffers();
			p.initPart(shaderManager, dataArray);
			
		});
		
		this.initTexture(); // FIXME : Do not allow to re-start renderer after stoping (for texture managing).
		
		this.initied = true;
	
	}
	
	public void stopRenderer() {
		
		if (!this.initied)
			throw new IllegalStateException("Can't stop " + this.getClass() + ", already stopped.");
		
		this.parts.values().forEach(EntityModelPart::stopPart);
		
		this.initied = false;
		
	}
	
	public void addPart(String id, EntityModelPart part) {
		
		if (this.initied)
			throw new IllegalStateException("Entity renderer already started, can't add part.");
		
		this.parts.put(id, part);
		
	}
	
	public void removePart(String id) {
		
		if (this.initied)
			throw new IllegalStateException("Entity renderer already started, can't remove part.");
		
		this.parts.remove(id);
		
	}
	
	public abstract void initTexture();
	public abstract Texture getTexture(E entity);
	public abstract void renderEntity(float alpha, ModelHandler model, E entity);
	
	@SuppressWarnings("unchecked")
	public void renderEntityUnsafe(float alpha, ModelHandler model, Entity entity) {
		this.renderEntity(alpha, model, (E) entity);
	}
	
	@SuppressWarnings("unchecked")
	public Texture getTextureUnsafe(Entity entity) {
		return this.getTexture((E) entity);
	}
	
}
