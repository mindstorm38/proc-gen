package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityModelPart;
import fr.theorozier.procgen.client.renderer.world.util.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.entity.Entity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.sutil.LazyLoadValue;

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
	
	private final LazyLoadValue<WorldRenderDataArray> optionalDataArray = new LazyLoadValue<WorldRenderDataArray>() {
		public WorldRenderDataArray create() {
			return new WorldRenderDataArray();
		}
	};
	
	protected WorldShaderManager shaderManager = null;
	
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
		
		//if (this.initied)
		//	throw new IllegalStateException("Entity renderer already started, can't add part.");
		
		this.parts.put(id, part);
		
		if (this.initied) {
			
			if (this.optionalDataArray.loaded())
				this.optionalDataArray.get().resetBuffers();
			
			part.initPart(this.shaderManager, this.optionalDataArray.get());
			
		}
		
	}
	
	public void removePart(String id) {
		
		//if (this.initied)
		//	throw new IllegalStateException("Entity renderer already started, can't remove part.");
		
		EntityModelPart part = this.parts.remove(id);
		
		if (this.initied && part != null) {
			part.stopPart();
		}
		
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
