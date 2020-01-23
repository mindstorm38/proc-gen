package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.GuiScrollableContainer;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import fr.theorozier.procgen.client.world.WorldList;
import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleplayerScreen extends Screen {
	
	private final WorldList worldList;
	
	private final GuiScrollableContainer scrollableContainer;
	
	private final GuiParent buttonsBlock;
	private final GuiButton doneButton;
	private final GuiButton createWorldButton;
	
	private final List<GuiTextBase> listLines;
	
	public SingleplayerScreen() {
		
		super("Singleplayer");
		
		this.worldList = ProcGenGame.getGameInstance().getWorldList();
		
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
		this.doneButton.setAnchor(-1, 0);
		this.doneButton.setPosition(5, 0);
		this.doneButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.doneButton);
		
		this.createWorldButton = new GuiButton(200, 40, "Create world ...");
		this.createWorldButton.setAnchor(1, 0);
		this.createWorldButton.setPosition(-5, 0);
		this.createWorldButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.buttonsBlock.addChild(this.createWorldButton);
		
		// Text lines for worlds (temporary for debug)
		this.listLines = new ArrayList<>();
		
	}
	
	@Override
	protected void loaded(Class<? extends GuiScene> previousScene) {
		
		super.loaded(previousScene);
		
		GuiParent scrollInternal = this.scrollableContainer.getInternal();
		
		List<WorldList.Entry> entries = new ArrayList<>(this.worldList.reload().values());
		
		int missingSize = entries.size() - this.listLines.size();
		
		if (missingSize > 0) {
			
			int yPos = 100 + this.listLines.size() * 40;
			
			for (int i = 0; i < missingSize; ++i) {
				
				GuiTextBase textBase = new GuiTextBase();
				textBase.setAnchor(-1, -1);
				textBase.setPosition(100, yPos);
				textBase.setHeight(20);
				this.listLines.add(textBase);
				
				yPos += 40;
				
			}
			
			scrollInternal.setHeight(yPos);
			
		}
		
		GuiTextBase text;
		for (int i = 0; i < this.listLines.size(); ++i) {
			
			text = this.listLines.get(i);
			
			if (i < entries.size()) {
				
				if (!scrollInternal.hasChild(text))
					scrollInternal.addChild(text);
				
				text.setText(entries.get(i).getName());
				
			} else {
				
				if (scrollInternal.hasChild(text))
					scrollInternal.removeChild(text);
				
			}
		
		}
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.isOrigin(this.doneButton)) {
			
			if (this.previousScene != null) {
				this.manager.loadScene(TitleScreen.class);
			}
			
		} else if (event.isOrigin(this.createWorldButton)) {
			this.manager.loadScene(CreateWorldScreen.class);
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
