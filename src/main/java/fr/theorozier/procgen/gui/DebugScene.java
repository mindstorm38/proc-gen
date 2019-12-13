package fr.theorozier.procgen.gui;

import fr.theorozier.procgen.ProcGenGame;
import fr.theorozier.procgen.renderer.world.WorldRenderer;
import fr.theorozier.procgen.util.MathUtils;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.biome.Biome;
import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextBase;
import io.msengine.client.util.camera.Camera3D;

public class DebugScene extends GuiScene {

	private final ProcGenGame game;
	private final WorldRenderer worldRenderer;
	
	private final GuiTextBase[] posTexts;
	
	public DebugScene() {
		
		this.game = (ProcGenGame) ProcGenGame.getCurrent();
		this.worldRenderer = this.game.getWorldRenderer();
		
		this.posTexts = new GuiTextBase[4];
		for (int i = 0; i < this.posTexts.length; i++) {
			
			this.posTexts[i] = new GuiTextBase();
			this.posTexts[i].setAnchor(-1f, -1f);
			this.posTexts[i].setPosition(10f, 10f + (i * 12f));
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
		
		World world = this.worldRenderer.getRenderingWorld();
		Biome biome = world == null ? null : world.getBiomeAt(MathUtils.fastfloor(cam.getX()), MathUtils.fastfloor(cam.getZ()));
		
		if (biome != null) {
			this.posTexts[3].setText("Biome : " + biome.getIdentifier());
		} else {
			this.posTexts[3].setText("");
		}
		
	}
	
}
