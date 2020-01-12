package fr.theorozier.procgen.client.gui.object;

import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import io.msengine.client.gui.GuiGrowingLineTexture;
import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiTextBase;
import io.msengine.client.gui.GuiTextColorable;
import io.msengine.client.renderer.texture.TexturePredefinedMap;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowMouseButtonEventListener;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import org.lwjgl.glfw.GLFW;

public class GuiButton extends GuiParent implements
		WindowMousePositionEventListener,
		WindowMouseButtonEventListener {
	
	private static final String STATE_DEFAULT  = "default";
	private static final String STATE_OVER     = "over";
	private static final String STATE_DISABLED = "disabled";
	
	private final GuiGrowingLineTexture textureObject;
	private final GuiTextColorable textObject;
	
	private boolean disabled;
	private boolean over;
	
	public GuiButton(float width, float height, String text) {
		
		this.textureObject = new GuiGrowingLineTexture();
		this.textureObject.setAnchor(-1, -1);
		this.textureObject.setPosition(0, 0);
		this.textureObject.setDefaultHeight(20);
		this.textureObject.setBordersWidth(2);
		this.addChild(this.textureObject);
		
		this.textObject = new GuiTextColorable(text);
		this.textObject.setAnchor(0, 0);
		this.textObject.setShadowOffsetX(this.textObject.getTextScale());
		this.textObject.setShadowOffsetY(this.textObject.getTextScale());
		this.addChild(this.textObject);
		
		this.setHeight(height);
		this.setWidth(width);
		
		this.disabled = false;
		this.over = false;
		
		this.updateStates();
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		TexturePredefinedMap map = GuiWidget.loadWidgetsTexture();
		this.textureObject.setStateTile(STATE_DEFAULT, map.getTile(GuiWidget.BUTTON_DEFAULT));
		this.textureObject.setStateTile(STATE_OVER, map.getTile(GuiWidget.BUTTON_OVER));
		this.textureObject.setStateTile(STATE_DISABLED, map.getTile(GuiWidget.BUTTON_DISABLED));
		
		Window.getInstance().addMousePositionEventListener(this);
		Window.getInstance().addMouseButtonEventListener(this);
		
	}
	
	@Override
	public void stop() {
		
		super.stop();
		
		Window.getInstance().removeMousePositionEventListener(this);
		Window.getInstance().removeMouseButtonEventListener(this);
		
	}
	
	@Override
	public void setWidth(float width) {
		
		super.setWidth(width);
		
		this.textureObject.setWidth(width);
		this.textObject.setXPos(width / 2f);
		
	}
	
	@Override
	public void setHeight(float height) {
		
		super.setHeight(height);
		
		this.textureObject.setHeight(height);
		this.textObject.setYPos((height / 2f) + 1);
		
	}
	
	@Override
	public float getAutoWidth() {
		return this.textureObject.getAutoWidth();
	}
	
	@Override
	public float getAutoHeight() {
		return this.textureObject.getAutoHeight();
	}
	
	public GuiTextBase getTextObject() {
		return this.textObject;
	}
	
	public boolean isDisabled() {
		return this.disabled;
	}
	
	public void setDisabled(boolean disabled) {
		
		if (this.disabled != disabled) {
			
			this.disabled = disabled;
			this.updateStates();
			
		}
		
	}
	
	public boolean isOver() {
		return this.over;
	}
	
	private void updateStates() {
		
		if (this.disabled) {
			
			this.textureObject.setState(STATE_DISABLED);
			this.textObject.setTextColor(115, 115, 115);
			
		} else if (this.over) {
			
			this.textureObject.setState(STATE_OVER);
			this.textObject.setTextColor(243, 245, 211);
			
		} else {
			
			this.textureObject.setState(STATE_DEFAULT);
			this.textObject.setTextColor(224, 224, 224);
			
		}
		
	}
	
	@Override
	public void windowMousePositionEvent(int x, int y) {
		
		if (this.renderable()) {
			
			float xOff = this.xOffset;
			float yOff = this.yOffset;
			boolean newOver = x >= xOff && y >= yOff && x < (xOff + this.width) && y < (yOff + this.height);
			
			if (newOver != this.over) {
				
				this.over = newOver;
				this.updateStates();
				
			}
			
		} else if (this.over) {
			
			this.over = false;
			this.updateStates();
			
		}
		
	}
	
	@Override
	public void windowMouseButtonEvent(int button, int action, int mods) {
		
		if (this.renderable()) {
			
			if (!this.disabled && this.over && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
				this.fireEvent(new GuiButtonActionEvent());
			}
			
		}
		
	}
	
}
