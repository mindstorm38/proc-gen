package fr.theorozier.procgen.client;

import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.client.world.WorldSinglePlayer;
import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.client.gui.DebugScene;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.common.entity.FallingBlockEntity;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.gen.beta.BetaChunkGenerator;
import io.msengine.client.game.DefaultRenderGame;
import io.msengine.client.game.RenderGameOptions;
import io.msengine.client.option.OptionKey;
import io.msengine.client.option.Options;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.window.CursorMode;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import io.msengine.client.util.camera.Camera3D;
import io.msengine.common.util.GameProfiler;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class ProcGenGame extends DefaultRenderGame<ProcGenGame> implements WindowKeyEventListener {
	
	public static final OptionKey KEY_FORWARD = new OptionKey("forward", GLFW.GLFW_KEY_W);
	public static final OptionKey KEY_BACKWARD = new OptionKey("backward", GLFW.GLFW_KEY_S);
	public static final OptionKey KEY_LEFT = new OptionKey("left", GLFW.GLFW_KEY_A);
	public static final OptionKey KEY_RIGHT = new OptionKey("right", GLFW.GLFW_KEY_D);
	public static final OptionKey KEY_JUMP = new OptionKey("jump", GLFW.GLFW_KEY_SPACE);
	public static final OptionKey KEY_CROUCH = new OptionKey("crouch", GLFW.GLFW_KEY_LEFT_SHIFT);
	
	public static final OptionKey KEY_GENERATE_CHUNKS = new OptionKey("generate_chunks", GLFW.GLFW_KEY_L);
	public static final OptionKey KEY_SPAWN_FALLING_BLOCK = new OptionKey("spawn_falling_block", GLFW.GLFW_KEY_I);
	
	private final WorldRenderer worldRenderer;
	private final WorldClient testWorld;
	
	private boolean escaped = false;
	
	public ProcGenGame(RenderGameOptions options) {
		
		super(options);
		
		this.worldRenderer = new WorldRenderer();
		this.testWorld = new WorldSinglePlayer(new WorldServer(BetaChunkGenerator.PROVIDER));
		
		this.options.addOption(KEY_FORWARD);
		this.options.addOption(KEY_BACKWARD);
		this.options.addOption(KEY_LEFT);
		this.options.addOption(KEY_RIGHT);
		this.options.addOption(KEY_JUMP);
		this.options.addOption(KEY_CROUCH);
		
		this.options.addOption(KEY_GENERATE_CHUNKS);
		
	}
	
	public WorldRenderer getWorldRenderer() {
		return this.worldRenderer;
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		TextureMap.setDebugAtlases(true);
		Blocks.computeStatesUids();
		this.profiler.setEnabled(true);
		GameProfiler.WARNING_TIME_LIMIT = 50000000L;
		
		this.window.addKeyEventListener(this);
		this.window.setFullscreen(Options.FULLSCREEN.getValue());
		
		this.worldRenderer.init();
		this.setEscaped(true);
		
		this.worldRenderer.renderWorld(this.testWorld);
		this.worldRenderer.getCamera().setTarget(0f, 100f, 0f, 0f, 0f);
		this.worldRenderer.getCamera().instantTarget();
		
		this.guiManager.registerSceneClass("debug", DebugScene.class);
		this.guiManager.loadScene("debug");
		
		glClearColor(0, 0, 0, 1);
		
	}
	
	@Override
	protected void stop() {
		
		this.worldRenderer.stop();
		
		try {
			this.options.save(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.stop();
		
	}
	
	@Override
	protected void render(float alpha) {
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		this.profiler.startSection("world_rendering");
		this.worldRenderer.render(alpha);
		
		this.profiler.endStartSection("gui");
		this.guiManager.render(alpha);
		this.profiler.endSection();
		
	}
	
	@Override
	protected void update() {
		
		this.profiler.startSection("world_renderer_update");
		this.worldRenderer.update();
		
		this.profiler.endStartSection("gui_update");
		this.guiManager.update();
		this.profiler.endSection();
		
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
		
		if (action == GLFW.GLFW_PRESS) {
			
			if (key == GLFW.GLFW_KEY_ESCAPE) {
				this.toggleEscaped();
			} else if (KEY_GENERATE_CHUNKS.isValid(key, scancode, mods)) {
				
				Camera3D cam = this.worldRenderer.getCamera();
				
				if (this.testWorld instanceof WorldSinglePlayer)
					((WorldSinglePlayer) this.testWorld).getServerWorld().loadNear(cam.getX(), cam.getZ());
				
			} else if (KEY_SPAWN_FALLING_BLOCK.isValid(key, scancode, mods)) {
				
				if (this.testWorld instanceof WorldSinglePlayer) {
					
					WorldServer serverWorld = ((WorldSinglePlayer) this.testWorld).getServerWorld();
					
					Camera3D cam = this.worldRenderer.getCamera();
					
					FallingBlockEntity entity = new FallingBlockEntity(serverWorld, serverWorld, (long) (Math.random() * 100000));
					entity.setPositionInstant(cam.getX() - 0.5f, cam.getY() - 2f, cam.getZ() - 0.5f);
					
					serverWorld.rawAddEntity(entity);
					
				}
				
			} else if (Options.TOGGLE_FULLSCREEN.isValid(key, scancode, mods)) {
				Options.FULLSCREEN.setValue(this.window.toggleFullscreen());
			}
			
		}
		
	}
	
}
