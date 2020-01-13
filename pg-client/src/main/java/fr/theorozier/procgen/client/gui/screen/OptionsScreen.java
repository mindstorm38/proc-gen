package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiParent;

public class OptionsScreen extends Screen {
	
	private final GuiParent buttonsBlock;
	private final GuiButton videoSettingsButton;
	private final GuiButton soundSettingsButton;
	private final GuiButton controlsButton;
	private final GuiButton doneButton;
	
	public OptionsScreen() {
		
		super("Options");
		
		// Buttons blocks
		this.buttonsBlock = new GuiParent();
		this.addChild(this.buttonsBlock);
		
		// Video settings button
		this.videoSettingsButton = new GuiButton(195, 40, "Video settings");
		this.videoSettingsButton.setAnchor(1, 0);
		this.videoSettingsButton.setPosition(-5, -50);
		this.buttonsBlock.addChild(this.videoSettingsButton);
		
		// Sound settings button
		this.soundSettingsButton = new GuiButton(195, 40, "Sound settings");
		this.soundSettingsButton.setAnchor(-1, 0);
		this.soundSettingsButton.setPosition(5, -50);
		this.buttonsBlock.addChild(this.soundSettingsButton);
		
		// Controls button
		this.controlsButton = new GuiButton(400, 40, "Controls");
		this.controlsButton.setAnchor(0, 0);
		this.controlsButton.setPosition(0, 0);
		this.buttonsBlock.addChild(this.controlsButton);
		
		// Back to title screen button
		this.doneButton = new GuiButton(300, 40, "Done");
		this.doneButton.setAnchor(0, 0);
		this.doneButton.setPosition(0, 50);
		this.doneButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.doneButton);
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		
		super.onSceneResized(width, height);
		
		this.buttonsBlock.setPosition(width / 2f, height / 2f);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.getOrigin() == this.doneButton) {
			
			if (this.previousScene != null) {
				this.manager.loadScene(this.previousScene);
			}
			
		}
		
	}
	
}
