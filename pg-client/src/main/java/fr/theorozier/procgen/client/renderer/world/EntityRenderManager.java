package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.renderer.entity.*;
import fr.theorozier.procgen.client.renderer.world.util.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.entity.*;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.common.util.GameProfiler;
import io.sutil.profiler.Profiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * Entity render manager singleton, instantiated in {@link WorldRenderer}.
 *
 * @author Theo Rozier
 *
 */
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
		this.addEntityRenderer(PrimedTNTEntity.class, new PrimedTNTRenderer());
		this.addEntityRenderer(PigEntity.class, new PigEntityRenderer());
		this.addEntityRenderer(PlayerEntity.class, new PlayerEntityRenderer());
		
	}
	
	/**
	 * Initialize this entity render manager.
	 */
	void init() {
		
		WorldShaderManager shaderManager = this.shaderManager;
		WorldRenderDataArray dataArray = new WorldRenderDataArray();
		
		for (EntityRenderer<?> renderer : this.entityRenderers.values())
			if (!renderer.isInitied())
				renderer.initRenderer(shaderManager, dataArray);
		
	}
	
	/**
	 * Stop the entity renderer.
	 */
	void stop() {
		
		for (EntityRenderer<?> renderer : this.entityRenderers.values())
			if (renderer.isInitied())
				renderer.stopRenderer();
		
	}
	
	/**
	 * Register a new entity renderer.
	 * @param entityClass The entity class.
	 * @param renderer The entity renderer.
	 * @param <E> Entity type.
	 */
	public <E extends Entity> void addEntityRenderer(Class<E> entityClass, EntityRenderer<? super E> renderer) {
		
		if (this.renderer.initialized())
			throw new IllegalStateException("Can't add entity renderer after world initialization.");
		
		this.entityRenderers.put(entityClass, renderer);
		
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Entity> EntityRenderer<E> getEntityRenderer(Class<E> entityClass) {
		return (EntityRenderer<E>) this.entityRenderers.get(entityClass);
	}
	
	/**
	 * Render entities.
	 * @param alpha Render lerp ratio.
	 */
	void render(float alpha) {
	
		ModelHandler model = this.model;
		EntityRenderer<?> renderer;
		ShaderSamplerObject sampler = null, newSampler;
		
		this.shaderManager.setTextureSampler(null);
		
		for (Map.Entry<Class<? extends Entity>, HashSet<Entity>> entitiesOfClass : this.entitiesByClasses.entrySet()) {
			
			if ((renderer = this.getEntityRenderer(entitiesOfClass.getKey())) != null) {
				
				for (Entity e : entitiesOfClass.getValue()) {
					
					newSampler = renderer.getTextureUnsafe(e);
					
					if (newSampler != sampler)
						this.shaderManager.setTextureSampler(sampler = newSampler);
					
					renderer.renderEntityUnsafe(alpha, model, e);
					
				}
			
			}
		
		}
		
		model.reset();
		model.apply();
		
	}
	
	/**
	 * Unload the entity renderer, clear all entities.
	 */
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
		
		HashSet<Entity> classEntities = this.entitiesByClasses.get(entity.getClass());
		if (classEntities != null) {
			classEntities.remove(entity);
		}
		
	}
	
}
