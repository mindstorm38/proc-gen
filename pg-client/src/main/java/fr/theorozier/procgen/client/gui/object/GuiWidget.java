package fr.theorozier.procgen.client.gui.object;

import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TexturePredefinedMap;
import io.msengine.client.renderer.texture.TextureWrapMode;

public final class GuiWidget {
	
	public static final String BUTTON_DISABLED = "button_disabled";
	public static final String BUTTON_DEFAULT  = "button_default";
	public static final String BUTTON_OVER     = "button_over";
	
	private static final TexturePredefinedMap WIDGETS_TEXTURE = new TexturePredefinedMap("textures/gui/widgets.png", 256, 128);
	private static final SimpleTexture TILED_BACKGROUND_TEXTURE = new SimpleTexture("textures/gui/tiled_background.png");
	
	static {
		
		WIDGETS_TEXTURE.newPixelTile(BUTTON_DISABLED, 0, 0, 200, 20);
		WIDGETS_TEXTURE.newPixelTile(BUTTON_DEFAULT, 0, 20, 200, 20);
		WIDGETS_TEXTURE.newPixelTile(BUTTON_OVER, 0, 40, 200, 20);
		
	}
	
	public static TexturePredefinedMap loadWidgetsTexture() {
		
		if (!TextureManager.getInstance().isTextureLoaded(WIDGETS_TEXTURE))
			TextureManager.getInstance().loadTexture(WIDGETS_TEXTURE);
		
		return WIDGETS_TEXTURE;
		
	}
	
	public static SimpleTexture loadTiledBackgroundTexture() {
		
		if (!TextureManager.getInstance().isTextureLoaded(TILED_BACKGROUND_TEXTURE)) {
			
			TextureManager.getInstance().loadTexture(TILED_BACKGROUND_TEXTURE);
			TILED_BACKGROUND_TEXTURE.getTextureObject().setWrap(TextureWrapMode.REPEAT, TextureWrapMode.REPEAT);
			
		}
		
		return TILED_BACKGROUND_TEXTURE;
		
	}
	
	private GuiWidget() {}
	
}
