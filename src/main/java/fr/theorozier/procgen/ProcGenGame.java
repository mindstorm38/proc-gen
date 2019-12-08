package fr.theorozier.procgen;

import fr.theorozier.procgen.gui.DebugScene;
import fr.theorozier.procgen.renderer.world.WorldRenderer;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.gen.beta.BetaChunkGenerator;
import io.msengine.client.game.DefaultRenderGame;
import io.msengine.client.game.RenderGameOptions;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.window.CursorMode;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import io.msengine.client.util.camera.Camera3D;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ProcGenGame extends DefaultRenderGame<ProcGenGame> implements WindowKeyEventListener {
	
	private final WorldRenderer worldRenderer;
	private final World testWorld;
	
	private boolean escaped = false;
	
	public ProcGenGame(RenderGameOptions options) {
		
		super(options);
		
		this.worldRenderer = new WorldRenderer();
		this.testWorld = new World(BetaChunkGenerator.PROVIDER);
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.worldRenderer;
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		TextureMap.setDebugAtlases(true);
		
		this.window.addKeyEventListener(this);
		
		this.worldRenderer.init();
		this.setEscaped(true);
		
		this.worldRenderer.renderWorld(this.testWorld);
		
		this.guiManager.registerSceneClass("debug", DebugScene.class);
		this.guiManager.loadScene("debug");
		
		glClearColor(0, 0, 0, 1);
		
	}
	
	@Override
	protected void stop() {
		
		this.worldRenderer.stop();
		
		super.stop();
		
	}
	
	@Override
	protected void render(float alpha) {
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		this.worldRenderer.render(alpha);
		this.guiManager.render(alpha);
		
	}
	
	@Override
	protected void update() {
		
		this.worldRenderer.update();
		this.guiManager.update();
		
		if (this.testWorld != null)
			this.testWorld.update();
		
	}
	
	public void setEscaped(boolean escaped) {
		
		this.escaped = escaped;
		this.worldRenderer.setEscaped(escaped);
		this.window.setCursorMode(escaped ? CursorMode.NORMAL : CursorMode.GRABBED);
		
	}
	
	public void toggleEscaped() {
		this.setEscaped(!this.escaped);
	}
	
	@Override
	public void windowKeyEvent(int key, int scancode, int action, int mods) {
		
		if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
			this.toggleEscaped();
		} else if (key == GLFW.GLFW_KEY_L && action == GLFW.GLFW_PRESS) {
			
			Camera3D cam = this.worldRenderer.getCamera();
			this.testWorld.loadNear(cam.getX(), cam.getZ());
		
		}
		
	}
	
}
