package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.GuiScrollableContainer;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiParent;

public class SingleplayerScreen extends Screen {
	
	private final GuiScrollableContainer scrollableContainer;
	
	private final GuiParent buttonsBlock;
	private final GuiButton doneButton;
	
	public SingleplayerScreen() {
		
		super("Singleplayer");
		
		// Scrollable
		this.scrollableContainer = new GuiScrollableContainer();
		this.scrollableContainer.setAnchor(-1, -1);
		this.scrollableContainer.setPosition(0, 100);
		this.addChild(this.scrollableContainer);
		
		// Buttons block
		this.buttonsBlock = new GuiParent();
		this.addChild(this.buttonsBlock);
		
		// Done button
		this.doneButton = new GuiButton(200, 40, "Done");
		this.doneButton.setAnchor(0, 0);
		this.doneButton.setPosition(0, 0);
		this.doneButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.doneButton);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.isOrigin(this.doneButton)) {
			
			if (this.previousScene != null) {
				this.manager.loadScene(this.previousScene);
			}
			
		}
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		
		super.onSceneResized(width, height);
		
		// Scrollable
		this.scrollableContainer.setSize(width, height - 200);
		
		// Buttons block
		this.buttonsBlock.setPosition(width / 2, height - 50);
		
	}
	
}
