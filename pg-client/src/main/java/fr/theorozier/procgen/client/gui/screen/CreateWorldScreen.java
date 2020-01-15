package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiTextInput;

public class CreateWorldScreen extends Screen {

	private final GuiParent mainBlock;
	private final GuiButton createButton;
	private final GuiButton cancelButton;
	
	private final GuiTextInput testInput;
	
	public CreateWorldScreen() {
		
		super("Create world...");
		
		// Main block
		this.mainBlock = new GuiParent();
		this.addChild(this.mainBlock);
		
		// Create world button
		this.createButton = new GuiButton(400, 40, "Create world ...");
		this.mainBlock.addChild(this.createButton);
		
		// Cancel button
		this.cancelButton = new GuiButton(300, 40, "Cancel");
		this.cancelButton.setPosition(0, 100);
		this.cancelButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.mainBlock.addChild(this.cancelButton);
		
		this.testInput = new GuiTextInput();
		this.testInput.setPosition(0, 200);
		this.testInput.setSize(200, 40);
		this.mainBlock.addChild(this.testInput);
		
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
