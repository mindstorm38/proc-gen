package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.ProcGenGame;
import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.chunk.Chunk;
import io.msengine.client.game.RenderGame;
import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import io.msengine.client.util.camera.Camera3D;
import io.sutil.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer implements ModelApplyListener,
		WindowFramebufferSizeEventListener,
		WindowMousePositionEventListener,
		WorldChunkLoadedListener {
	
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
	
	private final ChunkRenderManager chunkRenderManager;
	
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
		
		this.chunkRenderManager = new ChunkRenderManager(this);
		
	}
	
	public Camera3D getCamera() {
		return this.camera;
	}
	
	public void init() {
	
		if (this.init)
			throw new IllegalStateException("World renderer already initialized.");
		
		this.window.addMousePositionEventListener(this);
		
		// Textures
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
			
			float speedMult = alpha * 1.0f;
			boolean changed = false;
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_F)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - MathHelper.PI_HALF) * speedMult);
				changed = true;
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_B)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() + MathHelper.PI_HALF) * speedMult);
				changed = true;
			}
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
				this.camera.addPosition(0, speedMult, 0);
				changed = true;
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_D)) {
				this.camera.addPosition(0, -speedMult, 0);
				changed = true;
			}
			
			if (this.window.isKeyPressed(GLFW.GLFW_KEY_C)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw() - Math.PI) * speedMult, 0f, (float) Math.sin(this.camera.getYaw() - Math.PI) * speedMult);
				changed = true;
			} else if (this.window.isKeyPressed(GLFW.GLFW_KEY_V)) {
				this.camera.addPosition((float) Math.cos(this.camera.getYaw()) * speedMult, 0f, (float) Math.sin(this.camera.getYaw()) * speedMult);
				changed = true;
			}
			
			this.camera.updateViewMatrix();
			
			if (changed) {
				this.chunkRenderManager.updateViewPosition(this.camera);
			}
			
		}
		
		glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
		
		this.updateGlobalMatrix();
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		this.shaderManager.use();
		this.renderSkyBox();
		this.renderChunks();
		this.shaderManager.end();
	
	}
	
	private void renderChunks() {
		
		this.shaderManager.setTextureSampler(this.terrainMap);
		
		glDisable(GL_BLEND);
		glDepthMask(true);
		this.chunkRenderManager.render(BlockRenderLayer.OPAQUE);
		this.chunkRenderManager.render(BlockRenderLayer.CUTOUT);
		
		glDepthMask(false);
		glEnable(GL_BLEND);
		BlendMode.TRANSPARENCY.use();
		this.chunkRenderManager.render(BlockRenderLayer.TRANSPARENT);
		
		glDepthMask(true);
		
	}
	
	private void renderSkyBox() {
		
		this.shaderManager.setTextureSampler(null);
		
		this.model.push().translate(this.camera.getX(), this.camera.getY(), this.camera.getZ()).apply();
		this.skyBox.render();
		this.model.pop();
	
	}
	
	public void update() {
		
		this.terrainMap.tick();
		this.chunkRenderManager.update();
		
	}
	
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
			this.chunkRenderManager.unload();
		
		}
		
		this.renderingWorld = world;
		this.ready = world != null;
		
		if (this.ready) {
			
			this.chunkRenderManager.updateViewPosition(this.camera);
			this.renderingWorld.addChunkLoadedListener(this);
			
		}
	
	}
	
	public World getRenderingWorld() {
		return this.renderingWorld;
	}
	
	/**
	 * Package private method.
	 * @return The world shader manager.
	 */
	Basic3DShaderManager getShaderManager() {
		return this.shaderManager;
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
			this.chunkRenderManager.chunkLoaded(chunk);
		}
		
	}
	
	@Override
	public void worldChunkUnloaded(World world, Chunk chunk) {
		
		if (this.renderingWorld == world) {
			this.chunkRenderManager.chunkUnloaded(chunk);
		}
		
	}
	
}
