package fr.theorozier.procgen.client.gui;

import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTexture;
import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;

public class TitleScreen extends GuiScene {
	
	private final SimpleTexture backgroundTexture = new SimpleTexture("textures/gui/title/background.png");
	private final GuiTexture backgroundImage = new GuiTexture();
	
	public TitleScreen() {
		
		this.backgroundImage.setAnchor(0, 0);
		this.backgroundImage.setPosition(0, 0);
		this.backgroundImage.setSize(100, 100);
		this.backgroundImage.setTexture(this.backgroundTexture, true);
		this.addChild(this.backgroundImage);
		
		this.addEventListener(GuiSceneResizedEvent.class, this::guiSceneResized);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		TextureManager.getInstance().loadTexture(this.backgroundTexture);
		
	}
	
	@Override
	public void update() {
		
		super.update();
		this.backgroundImage.setSize(100, 100);
		
		
	}
	
	private void guiSceneResized(GuiSceneResizedEvent event) {
		System.out.println(event.getWidth() + "/" + event.getHeight());
	}
	
}
