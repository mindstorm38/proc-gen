package fr.theorozier.procgen.client.gui;

import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextBase;
import io.msengine.client.gui.GuiTexture;
import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;

public class TitleScreen extends GuiScene {
	
	private static final String[] DISCLAIMERS = { "Minecraft is a registered trademark of Mojang AB", "Unofficial Minecraft clone" };
	
	private final SimpleTexture backgroundTexture = new SimpleTexture("textures/gui/title/background.png");
	
	private final GuiTexture backgroundImage;
	private final GuiTextBase versionText;
	private final GuiParent disclaimerBlock;
	
	public TitleScreen() {
		
		this.backgroundImage = new GuiTexture();
		this.backgroundImage.setAnchor(0, 0);
		this.addChild(this.backgroundImage);
		
		this.versionText = new GuiTextBase("ProcGen Beta 0.1.2");
		this.versionText.setAnchor(-1, 1);
		this.versionText.setXPos(12);
		this.addChild(this.versionText);
		
		this.disclaimerBlock = new GuiParent();
		this.addChild(this.disclaimerBlock);
		
		GuiTextBase[] disclaimerTexts = new GuiTextBase[DISCLAIMERS.length];
		
		for (int i = 0; i < DISCLAIMERS.length; ++i) {
			
			disclaimerTexts[i] = new GuiTextBase(DISCLAIMERS[i]);
			disclaimerTexts[i].setAnchor(1, 1);
			disclaimerTexts[i].setPosition(0, (-i * (disclaimerTexts[i].getHeight() + 4)));
			this.disclaimerBlock.addChild(disclaimerTexts[i]);
			
		}
		
		this.addEventListener(GuiSceneResizedEvent.class, this::onSceneResized);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		TextureManager.getInstance().loadTexture(this.backgroundTexture);
		this.backgroundImage.setTexture(this.backgroundTexture, true);
		
	}
	
	private void onSceneResized(GuiSceneResizedEvent event) {
		
		float screenWidth = (float) event.getWidth();
		float screenHeight = (float) event.getHeight();
		
		// Background Image //
		this.backgroundImage.setPosition(screenWidth * 0.5f, screenHeight * 0.5f);
		
		float sceneRatio = screenWidth / screenHeight;
		float backgroundRatio = this.backgroundTexture.getWidth() / (float) this.backgroundTexture.getHeight();
		
		if (sceneRatio > backgroundRatio) {
			this.backgroundImage.setSize(screenWidth, screenWidth / backgroundRatio);
		} else {
			this.backgroundImage.setSize(screenHeight * backgroundRatio, screenHeight);
		}
		
		// Version Text //
		this.versionText.setYPos(screenHeight - 8);
		
		// Disclaimer Texts //
		this.disclaimerBlock.setPosition(screenWidth - 12, screenHeight - 8);
		
	}
	
}
