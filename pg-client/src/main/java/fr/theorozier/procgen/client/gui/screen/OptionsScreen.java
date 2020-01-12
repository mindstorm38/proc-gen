package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiParent;

public class OptionsScreen extends Screen {
	
	private final GuiParent buttonsBlock;
	private final GuiButton doneButton;
	
	public OptionsScreen() {
		
		super(true);
		
		// Buttons blocks
		this.buttonsBlock = new GuiParent();
		this.addChild(this.buttonsBlock);
		
		// Back to title screen button
		this.doneButton = new GuiButton(200, 40, "Done");
		this.doneButton.setAnchor(0, 0);
		this.doneButton.setPosition(0, 0);
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
