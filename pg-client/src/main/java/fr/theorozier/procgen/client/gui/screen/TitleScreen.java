package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.*;
import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;

public class TitleScreen extends GuiScene {
	
	private static final String[] DISCLAIMERS = { "Minecraft is a registered trademark of Mojang AB", "Unofficial Minecraft clone" };
	
	private final SimpleTexture backgroundTexture = new SimpleTexture("textures/gui/title/background.png");
	
	private final GuiTexture backgroundImage;
	private final GuiTextBase versionText;
	private final GuiParent disclaimerBlock;
	
	private final GuiParent buttonsBlock;
	private final GuiButton singleplayerButton;
	private final GuiButton multiplayerButton;
	private final GuiButton optionsButton;
	private final GuiButton quitButton;
	
	public TitleScreen() {
		
		// Background
		this.backgroundImage = new GuiTexture();
		this.backgroundImage.setAnchor(0, 0);
		this.addChild(this.backgroundImage);
		
		// Version
		this.versionText = new GuiTextBase("ProcGen Beta 0.1.2");
		this.versionText.setAnchor(-1, 1);
		this.versionText.setXPos(12);
		this.addChild(this.versionText);
		
		// Disclaimer
		this.disclaimerBlock = new GuiParent();
		this.addChild(this.disclaimerBlock);
		
		GuiTextBase[] disclaimerTexts = new GuiTextBase[DISCLAIMERS.length];
		
		for (int i = 0; i < DISCLAIMERS.length; ++i) {
			
			disclaimerTexts[i] = new GuiTextBase(DISCLAIMERS[i]);
			disclaimerTexts[i].setAnchor(1, 1);
			disclaimerTexts[i].setPosition(0, (-i * (disclaimerTexts[i].getHeight() + 4)));
			this.disclaimerBlock.addChild(disclaimerTexts[i]);
			
		}
		
		// Buttons
		this.buttonsBlock = new GuiParent();
		this.addChild(this.buttonsBlock);
		
		this.singleplayerButton = new GuiButton(360, 40, "Singleplayer");
		this.singleplayerButton.setAnchor(0, 0);
		this.singleplayerButton.setPosition(0, -50);
		this.singleplayerButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.singleplayerButton);
		
		this.multiplayerButton = new GuiButton(360, 40, "Multiplayer");
		this.multiplayerButton.setAnchor(0, 0);
		this.multiplayerButton.setPosition(0, 0);
		this.multiplayerButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.multiplayerButton);
		
		this.optionsButton = new GuiButton(175, 40, "Options");
		this.optionsButton.setAnchor(1, 0);
		this.optionsButton.setPosition(-5, 50);
		this.optionsButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.optionsButton);
		
		this.quitButton = new GuiButton(175, 40, "Quit game");
		this.quitButton.setAnchor(-1, 0);
		this.quitButton.setPosition(5, 50);
		this.quitButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.quitButton);
		
		// Other events
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
		
		// Buttons //
		this.buttonsBlock.setPosition(screenWidth / 2f, screenHeight / 2f);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.getOrigin() == this.singleplayerButton) {
			System.out.println("singleplayer button clicked");
		} else if (event.getOrigin() == this.multiplayerButton) {
			System.out.println("multiplayer button clicked");
		} else if (event.getOrigin() == this.optionsButton) {
			System.out.println("options button clicked");
		} else if (event.getOrigin() == this.quitButton) {
			ProcGenGame.getCurrent().stopRunning();
		}
		
	}
	
}
