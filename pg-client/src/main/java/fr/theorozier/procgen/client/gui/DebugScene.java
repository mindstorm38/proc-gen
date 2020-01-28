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
import io.sutil.math.MathHelper;

public class DebugScene extends GuiScene {

	private final ProcGenGame game;
	private final WorldRenderer worldRenderer;
	
	private final GuiTextBase[] posTexts;
	
	public DebugScene() {
		
		this.game = (ProcGenGame) ProcGenGame.getCurrent();
		this.worldRenderer = this.game.getWorldRenderer();
		
		this.posTexts = new GuiTextBase[6];
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
		
		if (biome != null) {
			this.posTexts[3].setText("Biome : " + biome.getIdentifier());
		} else {
			this.posTexts[3].setText("");
		}
		
		BlockState state = world == null ? null : world.getBlockAt(MathHelper.floorFloatInt(cam.getX()), MathHelper.floorFloatInt(cam.getY()), MathHelper.floorFloatInt(cam.getZ()));
		this.posTexts[4].setText("Block : " + state);
		
		if (world instanceof WorldSinglePlayer) {
			
			this.posTexts[5].setText("Heightmap : " + ((WorldSinglePlayer) world).getServerWorld().getHeightAt(Heightmap.Type.WORLD_BASE_SURFACE, MathHelper.floorFloatInt(cam.getX()), MathHelper.floorFloatInt(cam.getZ())));
			
		}
		
	}
	
}
