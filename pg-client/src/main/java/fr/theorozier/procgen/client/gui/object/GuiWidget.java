package fr.theorozier.procgen.client.gui.object;

import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TexturePredefinedMap;

public final class GuiWidget {
	
	public static final String BUTTON_DISABLED = "button_disabled";
	public static final String BUTTON_DEFAULT  = "button_default";
	public static final String BUTTON_OVER     = "button_over";
	
	private static final TexturePredefinedMap WIDGETS_TEXTURE = new TexturePredefinedMap("textures/gui/widgets.png", 256, 128);
	
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
	
	private GuiWidget() {}
	
}
