package fr.theorozier.procgen.client.gui;

import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextureMosaic;

public abstract class Screen extends GuiScene {
	
	private final GuiTextureMosaic backgroundMosaic;
	
	public Screen(boolean enableBackgroundMosaic) {
		
		if (enableBackgroundMosaic) {
			this.backgroundMosaic = new GuiTextureMosaic();
			this.addChild(this.backgroundMosaic);
		} else {
			this.backgroundMosaic = null;
		}
		
	}
	
}
