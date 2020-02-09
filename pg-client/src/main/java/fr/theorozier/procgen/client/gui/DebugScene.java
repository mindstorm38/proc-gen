package fr.theorozier.procgen.client.gui;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.client.world.WorldClient;
import fr.theorozier.procgen.client.world.WorldSinglePlayer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.chunk.Heightmap;
import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextBase;
import io.msengine.client.util.camera.Camera3D;
import io.msengine.common.util.GameProfiler;
import io.sutil.math.MathHelper;
import io.sutil.profiler.Profiler;
import io.sutil.profiler.ProfilerSection;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class DebugScene extends GuiScene {

	private static final Profiler PROFILER = GameProfiler.getInstance();
	
	private final ProcGenGame game;
	private final WorldRenderer worldRenderer;
	
	private final GuiTextBase[] posTexts;
	
	public DebugScene() {
		
		this.game = (ProcGenGame) ProcGenGame.getCurrent();
		this.worldRenderer = this.game.getWorldRenderer();
		
		this.posTexts = new GuiTextBase[36];
		for (int i = 0; i < this.posTexts.length; i++) {
			
			this.posTexts[i] = new GuiTextBase();
			this.posTexts[i].setAnchor(-1f, -1f);
			this.posTexts[i].setPosition(20f, 20f + (i * 24f));
			this.posTexts[i].setHeight(20);
			this.addChild(this.posTexts[i]);
			
		}
		
	}
	
	@Override
	public void update() {
		
		super.update();
		
		Camera3D cam = this.worldRenderer.getCamera();
		this.posTexts[0].setText("X: " + cam.getX());
		this.posTexts[1].setText("Y: " + cam.getY());
		this.posTexts[2].setText("Z: " + cam.getZ());
		
		WorldClient world = this.worldRenderer.getRenderingWorld();
		Biome biome = world == null ? null : world.getBiomeAt(MathHelper.floorFloatInt(cam.getX()), MathHelper.floorFloatInt(cam.getZ()));
		
		this.posTexts[3].setText("Biome : " + (biome == null ? "null" : biome.getIdentifier()));
		
		BlockState state = world == null ? null : world.getBlockAt(MathHelper.floorFloatInt(cam.getX()), MathHelper.floorFloatInt(cam.getY()), MathHelper.floorFloatInt(cam.getZ()));
		this.posTexts[4].setText("Block : " + state);
		
		if (world instanceof WorldSinglePlayer) {
			
			this.posTexts[5].setText("Heightmap : " + ((WorldSinglePlayer) world).getServerWorld().getHeightAt(Heightmap.Type.WORLD_BASE_SURFACE, MathHelper.floorFloatInt(cam.getX()), MathHelper.floorFloatInt(cam.getZ())));
			this.posTexts[6].setText("Seed : " + ((WorldSinglePlayer) world).getServerWorld().getSeed());
			
		}
		
		int idx = 8;
		for (String line : PROFILER.getSummaryString().split("\n")) {
			this.posTexts[idx++].setText(line);
			if (idx >= this.posTexts.length) {
				break;
			}
		}
		
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
	
	private static String getSectionDebug(ProfilerSection sec) {
		return sec == null ? "no_sec" : ("(sect:" + sec.repr() + ", avg:" + DECIMAL_FORMAT.format(sec.getAverageTimeMillis()) + ", last:" + DECIMAL_FORMAT.format(sec.getLastTimeMillis()) + ")");
	}
	
}
