package fr.theorozier.procgen.client.gui;

import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextColorable;
import io.msengine.client.gui.GuiTextureMosaic;
import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TextureWrapMode;

public abstract class Screen extends GuiScene {
	
	private static final SimpleTexture TILED_BACKGROUND_TEXTURE = new SimpleTexture("textures/gui/tiled_background.png");
	
	private final GuiTextureMosaic backgroundMosaic;
	private final GuiTextColorable titleText;
	
	public Screen(boolean enableBackgroundMosaic, String title) {
		
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
		
		if (title == null) {
			this.titleText = null;
		} else {
			
			this.titleText = new GuiTextColorable(title);
			this.titleText.setAnchor(0, -1);
			this.titleText.setYPos(40);
			this.titleText.setTextScale(3);
			this.titleText.setTextColor(230, 230, 230);
			this.titleText.setShadowOffset(3, 3);
			this.addChild(this.titleText);
			
		}
		
		this.addEventListener(GuiSceneResizedEvent.class, this::onSceneResized);
		
	}
	
	public Screen(boolean enableBackgroundMosaic) {
		this(enableBackgroundMosaic, null);
	}
	
	public Screen(String title) {
		this(true, title);
	}
	
	public Screen() {
		this(true, null);
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
		
		if (this.titleText != null)
			this.titleText.setXPos(width / 2f);
		
	}
	
}
