package fr.theorozier.procgen.client;

import fr.theorozier.procgen.client.gui.screen.*;
import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.client.world.WorldList;
import fr.theorozier.procgen.client.world.WorldSinglePlayer;
import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.client.gui.DebugScene;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.entity.LiveEntity;
import fr.theorozier.procgen.common.entity.MotionEntity;
import fr.theorozier.procgen.common.entity.PigEntity;
import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.gen.WorldGenerators;
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

import java.io.File;
import java.io.IOException;
import java.util.Random;

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
	
	public static final OptionKey KEY_EHEAD_YAW_INC = new OptionKey("entity_head_yaw_inc", GLFW.GLFW_KEY_RIGHT);
	public static final OptionKey KEY_EHEAD_YAW_DEC = new OptionKey("entity_head_yaw_dec", GLFW.GLFW_KEY_LEFT);
	public static final OptionKey KEY_EHEAD_PITCH_INC = new OptionKey("entity_head_pitch_inc", GLFW.GLFW_KEY_UP);
	public static final OptionKey KEY_EHEAD_PITCH_DEC = new OptionKey("entity_head_pitch_dec", GLFW.GLFW_KEY_DOWN);
	
	public static final OptionKey KEY_ENTITY_DEBUG = new OptionKey("entity_debug", GLFW.GLFW_KEY_P);
	public static final OptionKey KEY_ENTITY_MOVE = new OptionKey("entity_move", GLFW.GLFW_KEY_K);
	
	private final WorldList worldList;
	private WorldDimensionManager servedWorldGroup;
	
	private final WorldRenderer worldRenderer;
	private final WorldClient testWorld;
	
	private boolean escaped = false;
	
	public ProcGenGame(RenderGameOptions options) {
		
		super(options);
		
		this.worldList = new WorldList(new File(this.getAppdata(), "worlds"));
		
		this.worldRenderer = new WorldRenderer();
		this.testWorld = new WorldSinglePlayer(new WorldServer(null, new Random().nextLong(), WorldGenerators.BETA_CHUNK_PROVIDER));
		
		this.options.addOption(KEY_FORWARD);
		this.options.addOption(KEY_BACKWARD);
		this.options.addOption(KEY_LEFT);
		this.options.addOption(KEY_RIGHT);
		this.options.addOption(KEY_JUMP);
		this.options.addOption(KEY_CROUCH);
		
		this.options.addOption(KEY_GENERATE_CHUNKS);
		this.options.addOption(KEY_SPAWN_FALLING_BLOCK);
		
		this.options.addOption(KEY_EHEAD_YAW_INC);
		this.options.addOption(KEY_EHEAD_YAW_DEC);
		this.options.addOption(KEY_EHEAD_PITCH_INC);
		this.options.addOption(KEY_EHEAD_PITCH_DEC);
		
		this.options.addOption(KEY_ENTITY_DEBUG);
		this.options.addOption(KEY_ENTITY_MOVE);
		
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
		this.guiManager.registerSceneClass("title", TitleScreen.class);
		this.guiManager.registerSceneClass("options", OptionsScreen.class);
		this.guiManager.registerSceneClass("sp_main", SingleplayerScreen.class);
		this.guiManager.registerSceneClass("sp_create_world", CreateWorldScreen.class);
		this.guiManager.registerSceneClass("mp_main", MultiplayerScreen.class);
		this.guiManager.loadScene("title");
		
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
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
		
		this.profiler.startSection("world_rendering");
		//this.worldRenderer.render(alpha);
		
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
	
	// World Management //
	
	public WorldList getWorldList() {
		return this.worldList;
	}
	
	public void createWorld(String name, long seed) {
	
	
	
	}
	
	// Legacy //
	
	public void setEscaped(boolean escaped) {
		
		this.escaped = escaped;
		this.worldRenderer.setEscaped(escaped);
		this.window.setCursorMode(escaped ? CursorMode.NORMAL : CursorMode.GRABBED);
		
	}
	
	public void toggleEscaped() {
		this.setEscaped(!this.escaped);
	}
	
	private float rotation = 0f;
	
	@Override
	public void windowKeyEvent(int key, int scancode, int action, int mods) {
		
		if (action == GLFW.GLFW_PRESS) {
			
			if (key == GLFW.GLFW_KEY_ESCAPE) {
				//this.toggleEscaped();
			} else if (KEY_GENERATE_CHUNKS.isValid(key, scancode, mods)) {
				
				Camera3D cam = this.worldRenderer.getCamera();
				
				if (this.testWorld instanceof WorldSinglePlayer)
					((WorldSinglePlayer) this.testWorld).getServerWorld().loadNear(cam.getX(), cam.getZ());
				
			} else if (KEY_SPAWN_FALLING_BLOCK.isValid(key, scancode, mods)) {
				
				if (this.testWorld instanceof WorldSinglePlayer) {
					
					WorldServer serverWorld = ((WorldSinglePlayer) this.testWorld).getServerWorld();
					
					Camera3D cam = this.worldRenderer.getCamera();
					
					PigEntity entity = new PigEntity(serverWorld, (long) (Math.random() * 10000000));
					entity.setPositionInstant(cam.getX(), cam.getY() - 2f, cam.getZ());
					entity.setRotation(this.rotation, 0);
					this.rotation += 0.1f;
					
					serverWorld.rawAddEntity(entity);
					
				}
				
			} else if (Options.TOGGLE_FULLSCREEN.isValid(key, scancode, mods)) {
				Options.FULLSCREEN.setValue(this.window.toggleFullscreen());
			} else {
				
				Entity entity = this.testWorld.getEntitiesView().isEmpty() ? null : this.testWorld.getEntitiesView().get(0);
				
				if (entity instanceof LiveEntity) {
					
					LiveEntity lentity = (LiveEntity) entity;
					
					if (KEY_EHEAD_YAW_INC.isValid(key, scancode, mods)) {
						lentity.setHeadRotation(lentity.getHeadYaw() + 0.1f, lentity.getHeadPitch());
					} else if (KEY_EHEAD_YAW_DEC.isValid(key, scancode, mods)) {
						lentity.setHeadRotation(lentity.getHeadYaw() - 0.1f, lentity.getHeadPitch());
					} else if (KEY_EHEAD_PITCH_INC.isValid(key, scancode, mods)) {
						lentity.setHeadRotation(lentity.getHeadYaw(), lentity.getHeadPitch() + 0.1f);
					} else if (KEY_EHEAD_PITCH_DEC.isValid(key, scancode, mods)) {
						lentity.setHeadRotation(lentity.getHeadYaw(), lentity.getHeadPitch() - 0.1f);
					}
					
				}
				
				if (entity != null) {
					
					if (KEY_ENTITY_DEBUG.isValid(key, scancode, mods)) {
						entity.debugToConsole();
					}
					
				}
				
				if (entity instanceof MotionEntity) {
					
					MotionEntity mentity = (MotionEntity) entity;
					
					if (KEY_ENTITY_MOVE.isValid(key, scancode, mods)) {
						
						if (mentity.hasMoved()) {
							mentity.setVelocity(0, 0, 0);
						} else {
							mentity.setVelocity(Math.random() * 0.2f - 0.1f, Math.random(), Math.random() * 0.2f - 0.1f);
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
