package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.*;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;

public class TitleScreen extends Screen {
	
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
		
		super(false);
		
		// Background
		this.backgroundImage = new GuiTexture();
		this.backgroundImage.setAnchor(0, 0);
		this.backgroundImage.setColorEnabled(true);
		this.backgroundImage.setColor(150, 150, 150);
		this.addChild(this.backgroundImage);
		
		// Version
		this.versionText = new GuiTextBase("ProcGen Beta 0.1.3");
		this.versionText.setHeight(20);
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
			disclaimerTexts[i].setHeight(20);
			disclaimerTexts[i].setPosition(0, (-i * (20 + 4)));
			this.disclaimerBlock.addChild(disclaimerTexts[i]);
			
		}
		
		// Buttons
		this.buttonsBlock = new GuiParent();
		this.addChild(this.buttonsBlock);
		
		this.singleplayerButton = new GuiButton(400, 40, "Singleplayer");
		this.singleplayerButton.setAnchor(0, 0);
		this.singleplayerButton.setPosition(0, -50);
		this.singleplayerButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.singleplayerButton);
		
		this.multiplayerButton = new GuiButton(400, 40, "Multiplayer");
		this.multiplayerButton.setAnchor(0, 0);
		this.multiplayerButton.setPosition(0, 0);
		this.multiplayerButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.multiplayerButton);
		
		this.optionsButton = new GuiButton(195, 40, "Options");
		this.optionsButton.setAnchor(1, 0);
		this.optionsButton.setPosition(-5, 50);
		this.optionsButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.optionsButton);
		
		this.quitButton = new GuiButton(195, 40, "Quit game");
		this.quitButton.setAnchor(-1, 0);
		this.quitButton.setPosition(5, 50);
		this.quitButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.quitButton);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		TextureManager.getInstance().loadTexture(this.backgroundTexture);
		this.backgroundImage.setTexture(this.backgroundTexture, true);
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		
		super.onSceneResized(width, height);
		
		// Background Image //
		this.backgroundImage.setPosition(width * 0.5f, height * 0.5f);
		
		float sceneRatio = width / height;
		float backgroundRatio = this.backgroundTexture.getWidth() / (float) this.backgroundTexture.getHeight();
		
		if (sceneRatio > backgroundRatio) {
			this.backgroundImage.setSize(width, width / backgroundRatio);
		} else {
			this.backgroundImage.setSize(height * backgroundRatio, height);
		}
		
		// Version Text //
		this.versionText.setYPos(height - 8);
		
		// Disclaimer Texts //
		this.disclaimerBlock.setPosition(width - 12, height - 8);
		
		// Buttons //
		this.buttonsBlock.setPosition(width / 2f, height / 2f);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.getOrigin() == this.singleplayerButton) {
			this.manager.loadScene(SingleplayerScreen.class);
		} else if (event.getOrigin() == this.multiplayerButton) {
			this.manager.loadScene(MultiplayerScreen.class);
		} else if (event.getOrigin() == this.optionsButton) {
			this.manager.loadScene(OptionsScreen.class);
		} else if (event.getOrigin() == this.quitButton) {
			ProcGenGame.getCurrent().stopRunning();
		}
		
	}
	
}
