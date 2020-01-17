package fr.theorozier.procgen.client.gui.object;

import io.msengine.client.gui.GuiGrowingLineTexture;
import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiTextColorable;
import io.msengine.client.gui.event.GuiTextInputChangedEvent;
import io.msengine.client.renderer.texture.TexturePredefinedMap;
import io.msengine.common.util.Color;

public class GuiTextInput extends GuiParent {
	
	private static final Color DEFAULT_PLACEHOLDER_COLOR = new Color(80, 80, 80);
	
	private final GuiGrowingLineTexture backgroundTexture;
	private final GuiTextColorable placeholder;
	
	private final io.msengine.client.gui.GuiTextInput input;
	
	public GuiTextInput(String placeholder) {
		
		this.backgroundTexture = new GuiGrowingLineTexture();
		this.backgroundTexture.setAnchor(-1, -1);
		this.backgroundTexture.setDefaultHeight(20);
		this.backgroundTexture.setBordersWidth(2);
		this.backgroundTexture.setState("main");
		this.addChild(this.backgroundTexture, 0);
		
		this.placeholder = new GuiTextColorable(placeholder);
		this.placeholder.setAnchor(-1, 0);
		this.placeholder.setIgnoreUnderline(true);
		this.placeholder.setTextColor(DEFAULT_PLACEHOLDER_COLOR);
		this.addChild(this.placeholder);
		
		this.input = new io.msengine.client.gui.GuiTextInput();
		this.input.setAnchor(-1, -1);
		this.input.addEventListener(GuiTextInputChangedEvent.class, this::textInputChanged);
		this.addChild(this.input);
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		TexturePredefinedMap map = GuiWidget.loadWidgetsTexture();
		this.backgroundTexture.setStateTile("main", map.getTile(GuiWidget.BUTTON_DISABLED));
		
	}
	
	@Override
	public void setWidth(float width) {
		
		super.setWidth(width);
		
		this.backgroundTexture.setWidth(width);
		this.updateInputWidth();
		
	}
	
	@Override
	public void setHeight(float height) {
		
		super.setHeight(height);
		
		float scale = height / this.backgroundTexture.getDefaultHeight();
		float fontHeight = scale * 8;
		float inputPos = scale * 6;
		
		this.backgroundTexture.setHeight(height);
		
		this.placeholder.setXPos(inputPos);
		this.placeholder.setYPos(height / 2);
		this.placeholder.setHeight(fontHeight);
		
		this.input.setXPos(inputPos);
		this.input.setScrollPadding(10 * scale);
		this.updateInputWidth();
		this.input.setHeight(height);
		this.input.getText().setHeight(fontHeight);
		this.input.getText().setShadowOffset(scale, scale);
		
	}
	
	private void updateInputWidth() {
		this.input.setWidth(this.width - (this.input.getXPos() * 2));
	}
	
	private void textInputChanged(GuiTextInputChangedEvent event) {
		this.placeholder.setVisible(event.getValue().isEmpty());
	}
	
}
