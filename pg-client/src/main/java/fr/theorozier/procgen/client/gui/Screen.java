package fr.theorozier.procgen.client.gui;

import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextureMosaic;
import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TextureWrapMode;

public abstract class Screen extends GuiScene {
	
	private static final SimpleTexture TILED_BACKGROUND_TEXTURE = new SimpleTexture("textures/gui/tiled_background.png");
	
	private final GuiTextureMosaic backgroundMosaic;
	
	public Screen(boolean enableBackgroundMosaic) {
		
		if (enableBackgroundMosaic) {
			
			this.backgroundMosaic = new GuiTextureMosaic();
			this.backgroundMosaic.setTileSize(64, 64);
			this.backgroundMosaic.setAnchor(-1, -1);
			this.backgroundMosaic.setPosition(0, 0);
			this.backgroundMosaic.setColorEnabled(true);
			this.backgroundMosaic.setColor(70, 70, 70);
			this.addChild(this.backgroundMosaic);
			
		} else {
			this.backgroundMosaic = null;
		}
		
		this.addEventListener(GuiSceneResizedEvent.class, this::onSceneResized);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		if (this.backgroundMosaic != null) {
			
			if (!TextureManager.getInstance().isTextureLoaded(TILED_BACKGROUND_TEXTURE)) {
				
				TextureManager.getInstance().loadTexture(TILED_BACKGROUND_TEXTURE);
				TILED_BACKGROUND_TEXTURE.getTextureObject().setWrap(TextureWrapMode.REPEAT, TextureWrapMode.REPEAT);
				
			}
			
			this.backgroundMosaic.setTexture(TILED_BACKGROUND_TEXTURE);
			
		}
		
	}
	
	private void onSceneResized(GuiSceneResizedEvent event) {
		this.onSceneResized(event.getWidth(), event.getHeight());
	}
	
	protected void onSceneResized(float width, float height) {
		
		if (this.backgroundMosaic != null)
			this.backgroundMosaic.setSize(width, height);
		
	}
	
}
