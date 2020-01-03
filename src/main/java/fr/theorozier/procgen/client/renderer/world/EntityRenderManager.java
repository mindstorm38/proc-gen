package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.renderer.entity.EntityRenderer;
import fr.theorozier.procgen.client.renderer.entity.FallingBlockEntityRenderer;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.entity.FallingBlockEntity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.common.util.GameProfiler;
import io.sutil.profiler.Profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityRenderManager {

	private final WorldRenderer renderer;
	private final Profiler profiler;
	private final ModelHandler model;
	private final WorldShaderManager shaderManager;
	
	private final HashMap<Class<? extends Entity>, EntityRenderer<?>> entityRenderers;
	
	private final HashMap<Long, Entity> entitiesById;
	private final HashMap<Class<? extends Entity>, HashSet<Entity>> entitiesByClasses;

	EntityRenderManager(WorldRenderer renderer) {
		
		this.renderer = renderer;
		this.profiler = GameProfiler.getInstance();
		this.model = renderer.getModelHandler();
		this.shaderManager = renderer.getShaderManager();
		
		this.entityRenderers = new HashMap<>();
		
		this.entitiesById = new HashMap<>();
		this.entitiesByClasses = new HashMap<>();
		
		this.addEntityRenderer(FallingBlockEntity.class, new FallingBlockEntityRenderer());
		
	}
	
	void init() {
		
		for (EntityRenderer<?> renderer : this.entityRenderers.values())
			if (!renderer.isInitied())
				renderer.initRenderer(this.shaderManager);
		
	}
	
	void stop() {
		
		for (EntityRenderer<?> renderer : this.entityRenderers.values())
			if (renderer.isInitied())
				renderer.stopRenderer();
		
	}
	
	public <E extends Entity> void addEntityRenderer(Class<E> entityClass, EntityRenderer<E> renderer) {
		this.entityRenderers.put(entityClass, renderer);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Entity> EntityRenderer<E> getEntityRenderer(Class<E> entityClass) {
		return (EntityRenderer<E>) this.entityRenderers.get(entityClass);
	}
	
	void render(float alpha) {
	
		ModelHandler model = this.model;
		EntityRenderer<?> renderer;
		
		for (Map.Entry<Class<? extends Entity>, HashSet<Entity>> entitiesOfClass : this.entitiesByClasses.entrySet()) {
			
			if ((renderer = this.getEntityRenderer(entitiesOfClass.getKey())) != null) {
				
				for (Entity e : entitiesOfClass.getValue()) {
					
					this.shaderManager.setTextureSampler(renderer.getTextureUnsafe(e));
					renderer.renderEntityUnsafe(alpha, model, e);
					
				}
			
			}
		
		}
		
	}
	
	void unload() {
		
		this.entitiesById.clear();
		
	}
	
	void addEntity(Entity entity) {
		
		if (this.entitiesById.put(entity.getUid(), entity) == null) {
			
			this.entitiesByClasses.computeIfAbsent(entity.getClass(), ec -> new HashSet<>())
					.add(entity);
			
		}
		
	}
	
	void removeEntity(Entity entity) {
		
		this.entitiesById.remove(entity.getUid());
		
	}
	
}
