package fr.theorozier.procgen.client.renderer.world;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.event.WorldEntityListener;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import io.msengine.client.game.RenderGame;
import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import io.msengine.common.util.GameProfiler;
import io.sutil.math.MathHelper;
import io.sutil.pool.FixedObjectPool;
import io.sutil.profiler.Profiler;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * The world renderer singleton (access via {@link ProcGenGame} singleton).
 *
 * @author Theo Rozier
 *
 */
public class WorldRenderer implements ModelApplyListener,
		WindowFramebufferSizeEventListener,
		WindowMousePositionEventListener,
		WorldLoadingListener,
		WorldChunkListener,
		WorldEntityListener {
	
	// CONSTS //
	
	private static final Profiler PROFILER = GameProfiler.getInstance();
	
	private static final int RENDER_OFFSET_BASE  = 2048;
	private static final int RENDER_OFFSET_SHIFT = 12; // Step : 4096 (2^12)
	
	public static final int RENDER_DISTANCE_MIN = 4;
	public static final int RENDER_DISTANCE_MAX = 64;
	
	// CLASS //
	
	private final Window window;
	
	private final WorldShaderManager shaderManager;
	private final TextureMap terrainMap;
	private final WorldSkyBox skyBox;
	
	private final ModelHandler model;
	private final WorldCamera camera;
	private final Matrix4f globalMatrix;
	private final Matrix4f projectionMatrix;
	
	private WorldClient renderingWorld;
	
	private boolean escaped;
	private int lastMouseX, lastMouseY;
	
	private boolean init = false;
	private boolean ready = false;
	
	private final ChunkRenderManager chunkRenderManager;
	private final EntityRenderManager entityRenderManager;
	
	private int renderDistance = 0;
	
	// private int renderOffsetX, renderOffsetZ;
	
	public WorldRenderer() {
		
		this.window = Window.getInstance();
		this.window.addFramebufferSizeEventListener(this);
		
		this.shaderManager = new WorldShaderManager();
		this.terrainMap = new TextureMap("textures/blocks", TextureMap.PNG_FILTER);
		this.skyBox = new WorldSkyBox(this.shaderManager);
		
		this.model = new ModelHandler(this);
		this.camera = new WorldCamera();
		this.globalMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();
		
		this.chunkRenderManager = new ChunkRenderManager(this);
		this.entityRenderManager = new EntityRenderManager(this);
		
		// this.renderOffsetX = 0;
		// this.renderOffsetZ = 0;
		
	}
	
	public WorldCamera getCamera() {
		return this.camera;
	}
	
	/**
	 * @return The chunk render manager singleton.
	 */
	public ChunkRenderManager getChunkRenderManager() {
		return chunkRenderManager;
	}
	
	/**
	 * @return The entity render manager singleton.
	 */
	public EntityRenderManager getEntityRenderManager() {
		return entityRenderManager;
	}
	
	/**
	 * Initialize the world renderer.<br>
	 * Add event listeners for window, initialize shader manager and all world rendering utilities.
	 * @throws IllegalStateException If the world is already initialized.
	 */
	public void init() {
	
		if (this.init)
			throw new IllegalStateException("World renderer already initialized.");
		
		this.window.addMousePositionEventListener(this);
		
		// Textures
		RenderGame.getCurrentRender().getTextureManager().loadTexture(this.terrainMap);
		
		this.shaderManager.build();
		this.shaderManager.setGlobalOffset(0, 0, 0);
		this.skyBox.init();
		
		this.camera.setSpeed(0.2f);
		this.camera.setTarget(0, 110, 0, 0, 0);
		
		this.chunkRenderManager.init();
		this.entityRenderManager.init();
		
		this.init = true;
		
		this.setRenderDistance(32);
		
	}
	
	/**
	 * @return True if the world renderer was initialized, even if called {@link #stop()}.
	 */
	public boolean isInitialized() {
		return this.init;
	}
	
	/**
	 * Stop all things initialized in {@link #init()}, can't re-init after stoped.
	 * @throws IllegalStateException If the world renderer is not already initialized.
	 */
	public void stop() {
		
		if (!this.init)
			throw new IllegalStateException("World renderer can't be stoped until initialized.");
		
		this.chunkRenderManager.stop();
		this.entityRenderManager.stop();
		
		this.skyBox.stop();
		this.shaderManager.delete();
		
		this.window.removeMousePositionEventListener(this);
		
	}
	
	/**
	 * Set world render distance.
	 * @param renderDistance The new distance.
	 */
	public void setRenderDistance(int renderDistance) {
		
		if (!this.init)
			throw new IllegalStateException("Can't set render distance if WorldRenderer is not initialized.");
		
		MathUtils.requireIntegerInRange(renderDistance, RENDER_DISTANCE_MIN, RENDER_DISTANCE_MAX, "Given render distance");
		this.renderDistance = renderDistance;
		this.chunkRenderManager.setRenderDistance(renderDistance);
		this.updateRenderSize();
		
	}
	
	/**
	 * Render the world.
	 * @param alpha Render lerp ratio.
	 */
	public void render(float alpha) {
		
		if (!this.ready)
			return;
		
		if (!this.escaped) {
			
			PROFILER.startSection("camera");
			
			float speedMult = alpha * 2.0f;
			boolean changed = false;
			
			if (this.window.isKeyPressed(ProcGenGame.KEY_FORWARD.getKeyCode())) {
				this.camera.addTarget((float) Math.cos(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult, 0, 0);
				changed = true;
			} else if (this.window.isKeyPressed(ProcGenGame.KEY_BACKWARD.getKeyCode())) {
				this.camera.addTarget((float) Math.cos(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult, 0, 0);
				changed = true;
			}
			
			if (this.window.isKeyPressed(ProcGenGame.KEY_JUMP.getKeyCode())) {
				this.camera.addTarget(0, speedMult, 0, 0, 0);
				changed = true;
			} else if (this.window.isKeyPressed(ProcGenGame.KEY_CROUCH.getKeyCode())) {
				this.camera.addTarget(0, -speedMult, 0, 0, 0);
				changed = true;
			}
			
			if (this.window.isKeyPressed(ProcGenGame.KEY_LEFT.getKeyCode())) {
				this.camera.addTarget((float) Math.cos(this.camera.getYaw() - Math.PI) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - Math.PI) * speedMult, 0, 0);
				changed = true;
			} else if (this.window.isKeyPressed(ProcGenGame.KEY_RIGHT.getKeyCode())) {
				this.camera.addTarget((float) Math.cos(this.camera.getYaw()) * speedMult, 0f, (float) Math.sin(this.camera.getYaw()) * speedMult, 0, 0);
				changed = true;
			}
			
			if (changed) {
				
				/*
				int newRoX = -((MathHelper.floorFloatInt(this.camera.getTargetX() + RENDER_OFFSET_BASE) >> RENDER_OFFSET_SHIFT) << RENDER_OFFSET_SHIFT);
				int newRoZ = -((MathHelper.floorFloatInt(this.camera.getTargetZ() + RENDER_OFFSET_BASE) >> RENDER_OFFSET_SHIFT) << RENDER_OFFSET_SHIFT);
				
				if (this.renderOffsetX != newRoX || this.renderOffsetZ != newRoZ) {
					
					this.renderOffsetX = newRoX;
					this.renderOffsetZ = newRoZ;
					
					this.chunkRenderManager.updateRenderOffset(newRoX, newRoZ);
					
					System.out.println("New render offset : " + newRoX + "/" + newRoZ);
					
				}
				*/
				
				PROFILER.startSection("update_view_pos");
				this.chunkRenderManager.updateViewPosition(this.camera);
				PROFILER.endSection();
				
				ProcGenGame.getGameInstance().getTestLoadingPosition().set(MathHelper.floorFloatInt(this.camera.getTargetX()), MathHelper.floorFloatInt(this.camera.getTargetZ()));
				
			}
			
			//this.camera.updateViewMatrix(alpha, this.renderOffsetX, 0, this.renderOffsetZ);
			//this.camera.updateRotatedViewMatrix(alpha);
			
			PROFILER.endSection();
			
		}
		
		float camX = this.camera.getLerpedX(alpha);
		float camY = this.camera.getLerpedY(alpha);
		float camZ = this.camera.getLerpedZ(alpha);
		
		Matrix4f view = this.camera.getViewMatrix();
		view.identity();
		view.rotateX(-this.camera.getLerpedPitch(alpha));
		view.rotateY(this.camera.getLerpedYaw(alpha));
		view.translate(0, -camY, 0);
		// view.translate(-this.camera.getLerpedX(alpha), -this.camera.getLerpedY(alpha), -this.camera.getLerpedZ(alpha));
		
		this.updateGlobalMatrix();
		this.model.apply();
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		this.shaderManager.resetGlobalColor();
		this.shaderManager.use();
		
		PROFILER.startSection("render_skybox");
		this.renderSkyBox(camY);
		
		PROFILER.endStartSection("render_chunks");
		// this.shaderManager.setGlobalOffset(-this.camera.getLerpedX(alpha), -this.camera.getLerpedY(alpha), -this.camera.getLerpedZ(alpha));
		this.renderChunks(alpha, camX, camZ);
		PROFILER.endSection();
		
		this.shaderManager.end();
	
	}
	
	private void renderChunks(float alpha, float camX, float camZ) {
		
		this.shaderManager.setTextureSampler(this.terrainMap);
		
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
		glDepthMask(true);
		this.chunkRenderManager.render(BlockRenderLayer.OPAQUE, camX, camZ);
		this.chunkRenderManager.render(BlockRenderLayer.CUTOUT, camX, camZ);
		
		glDisable(GL_CULL_FACE);
		this.chunkRenderManager.render(BlockRenderLayer.CUTOUT_NOT_CULLED, camX, camZ);
		this.entityRenderManager.render(alpha, camX, camZ);
		
		this.shaderManager.setTextureSampler(this.terrainMap);
		
		glEnable(GL_CULL_FACE);
		glDepthMask(false);
		glEnable(GL_BLEND);
		BlendMode.TRANSPARENCY.use();
		this.chunkRenderManager.render(BlockRenderLayer.TRANSPARENT, camX, camZ);
		
		glDepthMask(true);
		
	}
	
	private void renderSkyBox(float camY) {
		
		this.shaderManager.setTextureSampler(null);
		
		this.model.push().translate(0, camY, 0).apply();
		this.skyBox.render();
		this.model.pop().apply();
	
	}
	
	/**
	 * Update the world renderer logic.
	 */
	public void update() {
		
		PROFILER.startSection("terrain_map");
		this.terrainMap.tick();
		PROFILER.endSection();
		
		if (this.ready) {
			this.chunkRenderManager.update();
		}
		
		this.camera.update();
		
	}
	
	/**
	 * Start to render a new world, and stop currently rendering world.
	 * @param world The world you want to start rendering, or Null to
	 *              just unload last rendered world.
	 */
	public void renderWorld(WorldClient world) {
		
		if (!this.init)
			return;
		
		if (this.renderingWorld != null) {
			
			this.renderingWorld.getEventManager().removeEventListener(WorldLoadingListener.class, this);
			this.renderingWorld.getEventManager().removeEventListener(WorldChunkListener.class, this);
			this.renderingWorld.getEventManager().removeEventListener(WorldEntityListener.class, this);
			this.chunkRenderManager.unload();
			this.entityRenderManager.unload();
		
		}
		
		this.renderingWorld = world;
		this.ready = world != null;
		
		if (world != null) {
			
			this.chunkRenderManager.updateViewPosition(this.camera);
			this.renderingWorld.getEntitiesView().forEach(this.entityRenderManager::addEntity);
			
			this.renderingWorld.getEventManager().addEventListener(WorldLoadingListener.class, this);
			this.renderingWorld.getEventManager().addEventListener(WorldChunkListener.class, this);
			this.renderingWorld.getEventManager().addEventListener(WorldEntityListener.class, this);
			
			this.camera.instantTarget();
			this.camera.updateViewMatrix();
			
		}
	
	}
	
	/**
	 * @return Current rendering world, or Null if no rendered world.
	 */
	public WorldClient getRenderingWorld() {
		return this.renderingWorld;
	}
	
	/**
	 * Package private method.
	 * @return The world shader manager.
	 */
	public WorldShaderManager getShaderManager() {
		return this.shaderManager;
	}
	
	public ModelHandler getModelHandler() {
		return this.model;
	}
	
	public TextureMap getTerrainMap() {
		return this.terrainMap;
	}
	
	/**
	 * Update global matrix and upload to shader manager uniform.
	 */
	private void updateGlobalMatrix() {
		
		this.globalMatrix.set(this.projectionMatrix);
		this.globalMatrix.mul(this.camera.getViewMatrix());
		
		this.shaderManager.setGlobalMatrix(this.globalMatrix);
		
	}
	
	@Override
	public void modelApply(Matrix4f model) {
		this.shaderManager.setModelMatrix(model);
	}
	
	/**
	 * Update render size.
	 * @param width New width.
	 * @param height New height.
	 */
	private void updateRenderSize(int width, int height) {
		
		this.projectionMatrix.identity();
		this.projectionMatrix.perspective((float) Math.toRadians(70f), (float) width / (float) height, 0.1f, (this.renderDistance << 4) * 4f);
		this.updateGlobalMatrix();
		
	}
	
	/**
	 * Update render size to window's size.
	 */
	private void updateRenderSize() {
		this.updateRenderSize(this.window.getWidth(), this.window.getHeight());
	}
	
	@Override
	public void windowFramebufferSizeChangedEvent(int width, int height) {
		this.updateRenderSize(width, height);
	}
	
	@Override
	public void windowMousePositionEvent(int x, int y) {
		
		int diffX = x - this.lastMouseX;
		int diffY = this.lastMouseY - y;
		
		this.lastMouseX = x;
		this.lastMouseY = y;
		
		if (!this.escaped)
			this.camera.addTarget(0, 0, 0, (float) Math.toRadians(diffX / 10f), (float) Math.toRadians(diffY / 10f));
		
	}
	
	public void setEscaped(boolean escaped) {
		this.escaped = escaped;
	}
	
	@Override
	public void worldSectionLoaded(WorldBase world, WorldSection section) {
		
		if (this.renderingWorld == world) {
			section.forEachChunk(this.chunkRenderManager::chunkLoaded);
		}
		
	}
	
	@Override
	public void worldSectionUnloaded(WorldBase world, ImmutableSectionPosition position) {
		
		if (this.renderingWorld == world) {
			try (FixedObjectPool<BlockPosition>.PoolObject pos = BlockPosition.POOL.acquire()) {
				for (int y = 0; y < world.getVerticalChunkCount(); ++y) {
					this.chunkRenderManager.chunkUnloaded(pos.get().set(position.getX(), y, position.getZ()));
				}
			}
		}
		
	}
	
	@Override
	public void worldChunkUpdated(WorldBase world, WorldChunk chunk) {
		
		if (this.renderingWorld == world) {
			this.chunkRenderManager.chunkUpdated(chunk);
		}
		
	}
	
	@Override
	public void worldChunkBlockChanged(WorldBase world, WorldChunk chunk, BlockPositioned pos, BlockState state) {
		
		if (this.renderingWorld == world) {
			this.chunkRenderManager.blockUpdated(chunk, pos, state);
		}
		
	}
	
	@Override
	public void worldEntityAdded(WorldBase world, Entity entity) {
		
		if (this.renderingWorld == world) {
			this.entityRenderManager.addEntity(entity);
		}
		
	}
	
	@Override
	public void worldEntityRemoved(WorldBase world, Entity entity) {
		
		if (this.renderingWorld == world) {
			this.entityRenderManager.removeEntity(entity);
		}
		
	}
	
}
