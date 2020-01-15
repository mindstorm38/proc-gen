package fr.theorozier.procgen.client.gui.object;

import io.msengine.client.gui.GuiColorCustom;
import io.msengine.client.gui.GuiTextureMosaic;
import io.msengine.client.renderer.texture.SimpleTexture;
import io.msengine.common.util.Color;

public class GuiScrollableContainer extends io.msengine.client.gui.GuiScrollableContainer {

	private static final Color SHADE_TRANSPARENT = new Color(0f, 0f, 0f, 0f);
	private static final Color SHADE_COLOR       = new Color(0f, 0f, 0f, 0.9f);
	
	private final GuiTextureMosaic backgroundMosaic;
	private final GuiColorCustom topShade;
	private final GuiColorCustom bottomShade;
	
	public GuiScrollableContainer() {
		
		this.backgroundMosaic = new GuiTextureMosaic();
		this.backgroundMosaic.setAnchor(-1, -1);
		this.backgroundMosaic.setPosition(0, 0);
		this.backgroundMosaic.setTileSize(64, 64);
		this.backgroundMosaic.setColorEnabled(true);
		this.backgroundMosaic.setColor(40, 40, 40);
		this.addChild(this.backgroundMosaic, this.getInternal());
		
		this.topShade = new GuiColorCustom(cidx -> cidx == 0 || cidx == 1 ? SHADE_COLOR : SHADE_TRANSPARENT);
		this.topShade.setAnchor(-1, -1);
		this.topShade.setPosition(0, 0);
		this.topShade.setHeight(20);
		this.addChild(this.topShade);
		
		this.bottomShade = new GuiColorCustom(cidx -> cidx == 2 || cidx == 3 ? SHADE_COLOR : SHADE_TRANSPARENT);
		this.bottomShade.setAnchor(-1, 1);
		this.bottomShade.setXPos(0);
		this.bottomShade.setHeight(20);
		this.addChild(this.bottomShade);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		SimpleTexture texture = GuiWidget.loadTiledBackgroundTexture();
		this.backgroundMosaic.setTexture(texture);
		
	}
	
	@Override
	public void setWidth(float width) {
		
		super.setWidth(width);
		
		this.backgroundMosaic.setWidth(width);
		this.topShade.setWidth(width);
		this.bottomShade.setWidth(width);
		
	}
	
	@Override
	public void setHeight(float height) {
		
		super.setHeight(height);
		
		this.backgroundMosaic.setHeight(height);
		this.bottomShade.setYPos(height);
		
	}
	
	@Override
	public void setScrollX(float x) {
		
		super.setScrollX(x);
		this.backgroundMosaic.setMosaicOffsetX(this.getInternal().getXPos());
		
	}
	
	@Override
	public void setScrollY(float y) {
		
		super.setScrollY(y);
		this.backgroundMosaic.setMosaicOffsetY(this.getInternal().getYPos());
		
	}
	
}
