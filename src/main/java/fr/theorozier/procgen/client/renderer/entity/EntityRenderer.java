package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityModelPart;
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
	private WorldShaderManager shaderManager = null;
	
	private final HashMap<String, EntityModelPart> parts = new HashMap<>();
	
	public void initRenderer(WorldShaderManager shaderManager) {
	
		if (this.initied)
			throw new IllegalStateException("This '" + this.getClass() + "' can't be initialized twice.");
		
		this.parts.values().forEach(p -> p.initPart(shaderManager));
		
		this.shaderManager = shaderManager;
		this.initied = true;
	
	}
	
	public void stopRenderer() {
		
		if (!this.initied)
			throw new IllegalStateException("Can't stop " + this.getClass() + ", already stopped.");
		
		this.parts.values().forEach(EntityModelPart::stopPart);
		
		this.shaderManager = null;
		this.initied = false;
		
	}
	
	public void addPart(String id, EntityModelPart part) {
		
		this.parts.put(id, part);
		
		if (this.initied)
			part.initPart(this.shaderManager);
		
	}
	
	public void removePart(String id) {
		
		EntityModelPart part = this.parts.remove(id);
		
		if (part != null && this.initied)
			part.stopPart();
		
	}
	
	public abstract void initTexture();
	public abstract Texture getTexture(E entity);
	public abstract void renderEntity(float alpha, ModelHandler model, E entity);
	
}
