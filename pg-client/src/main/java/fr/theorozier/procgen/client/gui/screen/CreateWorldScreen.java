package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.GuiTextInput;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiParent;

public class CreateWorldScreen extends Screen {

	private final GuiParent mainBlock;
	private final GuiTextInput worldNameInput;
	private final GuiTextInput worldSeedInput;
	private final GuiButton createButton;
	private final GuiButton cancelButton;
	
	public CreateWorldScreen() {
		
		super("Create world...");
		
		// Main block
		this.mainBlock = new GuiParent();
		this.addChild(this.mainBlock);
		
		// World name input
		this.worldNameInput = new GuiTextInput("Enter the world name");
		this.worldNameInput.setPosition(0, -100);
		this.worldNameInput.setSize(400, 40);
		this.mainBlock.addChild(this.worldNameInput);
		
		// World seed input
		this.worldSeedInput = new GuiTextInput("World seed (optional)");
		this.worldSeedInput.setPosition(0, -50);
		this.worldSeedInput.setSize(400, 40);
		this.mainBlock.addChild(this.worldSeedInput);
		
		// Create world button
		this.createButton = new GuiButton(400, 40, "Create world ...");
		this.createButton.setPosition(0, 0);
		this.mainBlock.addChild(this.createButton);
		
		// Cancel button
		this.cancelButton = new GuiButton(300, 40, "Cancel");
		this.cancelButton.setPosition(0, 100);
		this.cancelButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.mainBlock.addChild(this.cancelButton);
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		
		super.onSceneResized(width, height);
		
		this.mainBlock.setPosition(width / 2f, height / 2f);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.isOrigin(this.cancelButton)) {
			if (this.previousScene != null) {
				this.manager.loadScene(this.previousScene);
			}
		}
		
	}
	
}
