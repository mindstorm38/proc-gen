package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.game.RenderGame;
import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import io.msengine.client.util.camera.Camera3D;
import io.sutil.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer implements ModelApplyListener,
		WindowFramebufferSizeEventListener,
		WindowMousePositionEventListener,
		WorldChunkLoadedListener {
	
	// These distances are squared, for optimisation.
	public static final int RENDER_DISTANCE = 16 * 16;
	public static final int UNLOAD_DISTANCE = 16 * 32;
	
	public static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;
	public static final int UNLOAD_DISTANCE_SQUARED = UNLOAD_DISTANCE * UNLOAD_DISTANCE;
	
	private final Window window;
	
	private final Basic3DShaderManager shaderManager;
	private final TextureMap terrainMap;
	private final WorldSkyBox skyBox;
	
	private final ModelHandler model;
	private final Camera3D camera;
	private final Matrix4f globalMatrix;
	private final Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;
	
	private World renderingWorld;
	
	private boolean escaped;
	private int lastMouseX, lastMouseY;
	
	private boolean init = false;
	private boolean ready = false;
	
	private final Map<BlockPosition, WorldChunkRenderer> chunkRenderers;
	private final List<BlockPosition> unloadingChunkRenderers;
	
	public WorldRenderer() {
		
		this.window = Window.getInstance();
		this.window.addFramebufferSizeEventListener(this);
		
		this.shaderManager = new Basic3DShaderManager("world", "world");
		this.terrainMap = new TextureMap("textures/blocks", TextureMap.PNG_FILTER);
		this.skyBox = new WorldSkyBox(this.shaderManager);
		
		this.model = new ModelHandler(this);
		this.camera = new Camera3D();
		this.globalMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();
		
		this.chunkRenderers = new HashMap<>();
		this.unloadingChunkRenderers = new ArrayList<>();
		
	}
	
	public Camera3D getCamera() {
		return this.camera;
	}
	
	public void init() {
	
		if (this.init)
			throw new IllegalStateException("World renderer already initialized.");
		
		this.window.addMousePositionEventListener(this);
		
		RenderGame.getCurrentRender().getTextureManager().loadTexture(this.terrainMap);
		
		this.shaderManager.build();
		this.skyBox.init();
		
		this.camera.setPosition(0, 110, 0);
		this.camera.updateViewMatrix();
		
		this.updateRenderSize(this.window);
		
		this.init = true;
		
	}
	
	public void stop() {
		
		if (!this.init)
			throw new IllegalStateException("World renderer can't be stoped until initialized.");
		
		this.skyBox.stop();
		this.shaderManager.delete();
		
		this.window.removeMousePositionEventListener(this);
		
		this.init = false;
		
	}
	
	public void render(float alpha) {
		
		if (!this.escaped) {
			
			float speedMult = alpha * 1.5f;
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_F)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult);
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_B)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult);
			}
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
				this.camera.addPosition(0, speedMult, 0);
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_D)) {
				this.camera.addPosition(0, -speedMult, 0);
			}
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_C)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() - Math.PI) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - Math.PI) * speedMult);
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_V)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw()) * speedMult, 0f, (float) Math.sin(this.camera.getYaw()) * speedMult);
			}
			
			this.camera.updateViewMatrix();
			this.refreshCameraRenderedChunks();
			
		}
		
		GL11.glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
		
		this.updateGlobalMatrix();
		
		glEnable(GL_CULL_FACE);
		
		this.shaderManager.use();
		this.renderSkyBox();
		this.renderChunks();
		this.shaderManager.end();
	
	}
	
	private void renderChunks() {
		
		glEnable(GL_DEPTH_TEST);
		
		this.shaderManager.setTextureSampler(this.terrainMap);
		
		this.chunkRenderers.forEach((pos, cr) -> {
			
			cr.checkLastNeighbours();
			cr.render(RENDER_DISTANCE_SQUARED);
			
		});
		
	}
	
	private void renderSkyBox() {
		
		glDisable(GL_DEPTH_TEST);
		
		this.shaderManager.setTextureSampler(null);
		
		this.model.push().translate(this.camera.getX(), this.camera.getY(), this.camera.getZ()).apply();
		this.skyBox.render();
		this.model.pop();
	
	}
	
	public void update() { }
	
	/**
	 * Start to render a new world, and stop currently rendering world.
	 * @param world The world you want to start rendering, or Null to
	 *              just unload last rendered world.
	 */
	public void renderWorld(World world) {
		
		if (!this.init)
			return;
		
		if (this.ready) {
			
			this.renderingWorld.removeChunkLoadedListener(this);
			this.chunkRenderers.values().forEach(WorldChunkRenderer::delete);
			this.chunkRenderers.clear();
		
		}
		
		this.renderingWorld = world;
		this.ready = world != null;
		
		if (this.ready) {
			
			this.refreshCameraRenderedChunks();
			this.renderingWorld.addChunkLoadedListener(this);
			
		}
	
	}
	
	public World getRenderingWorld() {
		return this.renderingWorld;
	}
	
	/**
	 * Internal method to get an existing chunk renderer.
	 * @param at The absolute position of the chunk.
	 * @return The chunk renderer, or Null if not existing.
	 */
	private WorldChunkRenderer getChunkRenderer(BlockPosition at) {
		return this.chunkRenderers.get(at);
	}
	
	/**
	 * For each chunks near the camera ({@link #RENDER_DISTANCE}), load it if not already loaded.
	 */
	private void refreshCameraRenderedChunks() {
		
		if (this.ready) {
			
			float cx = this.camera.getX();
			float cy = this.camera.getY();
			float cz = this.camera.getZ();
			
			this.renderingWorld.forEachChunkNear(cx, cy, cz, RENDER_DISTANCE, this::loadChunkRenderer);
			
			this.chunkRenderers.values().forEach(cr -> {
				
				if (cr.updateDistanceToCamera(cx, cy, cz) > UNLOAD_DISTANCE_SQUARED) {
					// System.out.println("Unloading CR " + cr.getChunkPosition());
					this.unloadingChunkRenderers.add(cr.getChunkPosition());
				}
				
			});
			
			if (this.unloadingChunkRenderers.size() != 0) {
				
				for (BlockPosition pos : this.unloadingChunkRenderers)
					this.chunkRenderers.remove(pos).delete();
				
				this.unloadingChunkRenderers.clear();
				
			}
			
		}
		
	}
	
	/**
	 * Load a chunk renderer if not already loaded.
	 * @param chunk The chunk to load.
	 */
	private void loadChunkRenderer(Chunk chunk) {
		
		BlockPosition pos = chunk.getChunkPosition();
		WorldChunkRenderer cr = this.chunkRenderers.get(pos);
		
		if (cr == null) {
			
			cr = new WorldChunkRenderer(this, chunk);
			this.chunkRenderers.put(chunk.getChunkPosition(), cr);
			
			cr.init();
			
			WorldChunkRenderer nb;
			
			for (Direction face : Direction.values()) {
				if ((nb = this.getChunkRenderer(pos.add(face.rx * 16, face.ry * 16, face.rz * 16))) != null) {
					
					nb.setNeighbour(face.oposite(), cr);
					cr.setNeighbour(face, nb);
					
				}
			}
			
			// System.out.println("Loading CR " + pos);
			
		}
		
	}
	
	/**
	 * Load a chunk renderer if not already loaded only if the chunk
	 * is near the camera ({@link #RENDER_DISTANCE}).
	 * @param chunk The chunk to test.
	 */
	private void checkChunkRenderer(Chunk chunk) {
	
		// Camera chunk position
		BlockPosition camcp = World.getChunkPosition(
				MathUtils.fastfloor(this.camera.getX()),
				MathUtils.fastfloor(this.camera.getY()),
				MathUtils.fastfloor(this.camera.getZ())
		);
		
		int xmin = camcp.getX() - RENDER_DISTANCE;
		int ymin = camcp.getY() - RENDER_DISTANCE;
		int zmin = camcp.getZ() - RENDER_DISTANCE;
		
		int xmax = camcp.getX() + RENDER_DISTANCE;
		int ymax = camcp.getY() + RENDER_DISTANCE;
		int zmax = camcp.getZ() + RENDER_DISTANCE;
		
		BlockPosition cp = chunk.getChunkPosition();
		
		if (cp.getX() >= xmin && cp.getX() <= xmax && cp.getY() >= ymin && cp.getY() <= ymax && cp.getZ() >= zmin && cp.getZ() <= zmax) {
			this.loadChunkRenderer(chunk);
		}
	
	}
	
	/**
	 * Package private method.
	 * @return The world shader manager.
	 */
	Basic3DShaderManager getShaderManager() {
		return this.shaderManager;
	}
	
	TextureMap getTerrainMap() {
		return this.terrainMap;
	}
	
	/**
	 * Update global matrix and upload to shader manager uniform.
	 */
	private void updateGlobalMatrix() {
		
		this.globalMatrix.set(this.projectionMatrix);
		this.globalMatrix.mul(this.camera.getViewMatrix());
		
		if (this.modelMatrix != null)
			this.globalMatrix.mul(this.modelMatrix);
		
		this.shaderManager.setGlobalMatrix(this.globalMatrix);
		
	}
	
	@Override
	public void modelApply(Matrix4f model) {
		
		this.modelMatrix = model;
		this.updateGlobalMatrix();
		
	}
	
	/**
	 * Update render size.
	 * @param width New width.
	 * @param height New height.
	 */
	private void updateRenderSize(int width, int height) {
		
		this.projectionMatrix.identity();
		this.projectionMatrix.perspective((float) Math.toRadians(70f), (float) width / (float) height, 0.1f, 16 * 256);
		this.updateGlobalMatrix();
		
	}
	
	/**
	 * Update render size to window's size.
	 * @param window The window.
	 */
	private void updateRenderSize(Window window) {
		this.updateRenderSize(window.getWidth(), window.getHeight());
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
			this.camera.addRotation((float) Math.toRadians(diffY / 10f), (float) Math.toRadians(diffX / 10f));
		
	}
	
	public void setEscaped(boolean escaped) {
		this.escaped = escaped;
	}
	
	@Override
	public void worldChunkLoaded(World world, Chunk chunk) {
		
		if (this.renderingWorld == world) {
			this.checkChunkRenderer(chunk);
		}
		
	}
	
	@Override
	public void worldChunkUnloaded(World world, Chunk chunk) {
		
		if (this.renderingWorld == world) {
			
			WorldChunkRenderer cr = this.chunkRenderers.get(chunk.getChunkPosition());
			
			if (cr != null) {
				
				this.chunkRenderers.remove(chunk.getChunkPosition());
				cr.delete();
				
			}
			
		}
		
	}
	
}
