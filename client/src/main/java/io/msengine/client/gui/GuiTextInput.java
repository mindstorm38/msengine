package io.msengine.client.gui;

import io.msengine.client.renderer.gui.GUIMaskRectangle;
import io.msengine.client.renderer.gui.GuiMask;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowCharEventListener;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import io.msengine.common.util.Color;
import io.sutil.ClipboardUtils;
import org.lwjgl.glfw.GLFW;

public class GuiTextInput extends GuiParent implements
		WindowCharEventListener,
		WindowKeyEventListener {
	
	private final InputText text;
	private final GuiColorSolid cursor;
	
	private final GUIMaskRectangle mask;
	private final GuiMask[] maskArray;
	
	private final GuiColorSolid debug;
	
	private final StringBuilder builder;
	private int cursorIndex = 0;
	
	private float scrollPadding = 10;
	private int cursorAnimation = 10;
	private int cursorTick = 0;
	
	public GuiTextInput() {
		
		this.text = new InputText();
		this.text.setAnchor(-1, 0);
		this.addChild(this.text);
		
		this.cursor = new GuiColorSolid();
		this.cursor.setAnchor(-1, 0);
		this.addChild(this.cursor);
		
		this.mask = new GUIMaskRectangle();
		this.maskArray = new GuiMask[] { this.mask };
		
		this.debug = new GuiColorSolid(Color.BLACK);
		this.debug.setAnchor(-1, -1);
		this.addChild(this.debug, 0);
		
		this.builder = new StringBuilder();
		
		this.updateCursor();
		this.textScaleUpdated();
		
	}
	
	@Override
	public void init() {
		
		super.init();
		
		this.mask.init();
		
		Window.getInstance().addCharEventListener(this);
		Window.getInstance().addKeyEventListener(this);
		
	}
	
	@Override
	public void stop() {
		
		Window.getInstance().removeCharEventListener(this);
		Window.getInstance().removeKeyEventListener(this);
		
		this.mask.stop();
		
		super.stop();
		
	}
	
	@Override
	public void render(float alpha) {
		this.renderer.mask(this.maskArray);
		super.render(alpha);
		this.renderer.unmask();
	}
	
	@Override
	public void update() {
		
		if (this.cursorTick++ > this.cursorAnimation) {
			
			this.cursorTick = 0;
			this.cursor.setVisible(!this.cursor.isVisible());
			
		}
		
		super.update();
		
	}
	
	@Override
	public void updateXOffset() {
		super.updateXOffset();
		this.mask.setX(this.xOffset);
		this.mask.setWidth(this.width);
	}
	
	@Override
	public void updateYOffset() {
		super.updateYOffset();
		this.mask.setY(this.yOffset);
		this.mask.setHeight(this.height);
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.debug.setWidth(width);
	}
	
	@Override
	public void setHeight(float height) {
		
		super.setHeight(height);
		this.debug.setHeight(height);
		this.text.setYPos(height / 2f);
		this.cursor.setYPos(height / 2f);
		
	}
	
	public GuiTextColorable getText() {
		return this.text;
	}
	
	public GuiColorSolid getCursor() {
		return this.cursor;
	}
	
	private void textScaleUpdated() {
		
		if (this.cursor != null) {
			
			float textScale = this.text.getTextScale();
			
			this.cursor.setSize(textScale, this.text.getHeight());
			this.text.setXPos(textScale);
			
		}
		
	}
	
	private void updateCursor() {
		
		float scrollPadding = this.scrollPadding;
		float charOffset = this.text.getCharOffset(this.cursorIndex - 1);
		float textScale = this.text.getTextScale();
		
		if (charOffset == 0)
			charOffset = -textScale;
		
		float realOffset = charOffset + this.text.getXPos();
		
		if (realOffset < scrollPadding) {
			this.text.setXPos(scrollPadding - charOffset);
		} else if (realOffset > (this.width - scrollPadding)) {
			this.text.setXPos(this.width - scrollPadding - charOffset);
		}
		
		this.cursor.setXPos(charOffset + this.text.getXPos());
		
	}
	
	private void updateText() {
		this.text.setText(this.builder.toString());
	}
	
	private void setCursorPosition(int index, boolean updateCursor) {
		
		if (this.cursorIndex != index) {
			
			this.cursorIndex = index;
			this.cursorTick = 0;
			
			if (updateCursor)
				this.updateCursor();
			
			if (!this.cursor.isVisible())
				this.cursor.setVisible(true);
			
		}
		
	}
	
	@Override
	public void windowCharEvent(char codepoint) {
	
		if (this.renderable()) {
			
			this.builder.insert(this.cursorIndex, codepoint);
			this.setCursorPosition(this.cursorIndex + 1, false);
			this.updateText();
			
		}
		
	}
	
	@Override
	public void windowKeyEvent(int key, int scancode, int action, int mods) {
	
		if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
			
			if (key == GLFW.GLFW_KEY_LEFT) { // Move Cursor Left
				
				if (this.cursorIndex > 0) {
					this.setCursorPosition(this.cursorIndex - 1, true);
				}
				
			} else if (key == GLFW.GLFW_KEY_RIGHT) { // Move Cursor Right
				
				if (this.cursorIndex < this.builder.length()) {
					this.setCursorPosition(this.cursorIndex + 1, true);
				}
			
			} else if (key == GLFW.GLFW_KEY_HOME) { // Move Cursor at Start
				
				if (this.cursorIndex != 0) {
					this.setCursorPosition(0, true);
				}
				
			} else if (key == GLFW.GLFW_KEY_END) { // Move Cursor at End
				
				int end = this.builder.length();
				
				if (this.cursorIndex != end) {
					this.setCursorPosition(end, true);
				}
				
			}  else if (key == GLFW.GLFW_KEY_BACKSPACE) { // Remove on left
				
				if (this.cursorIndex > 0) {
					
					this.builder.deleteCharAt(this.cursorIndex - 1);
					this.setCursorPosition(this.cursorIndex - 1, false);
					this.updateText();
					
				}
				
			} else if (key == GLFW.GLFW_KEY_DELETE) { // Delete on right
				
				if (this.cursorIndex != this.builder.length()) {
					
					this.builder.deleteCharAt(this.cursorIndex);
					this.updateText();
					
				}
				
			} else if (key == GLFW.GLFW_KEY_V && Window.isModActive(mods, GLFW.GLFW_MOD_CONTROL)) { // Paste from clipboard
				
				String clipboard = ClipboardUtils.getClipboardString();
				this.builder.insert(this.cursorIndex, clipboard);
				
				this.setCursorPosition(this.cursorIndex + clipboard.length(), false);
				this.updateText();
			
			}
			
		}
	
	}
	
	private class InputText extends GuiTextColorable {
		
		@Override
		protected void updateTextBuffers() {
			super.updateTextBuffers();
			GuiTextInput.this.updateCursor();
		}
		
		@Override
		public void updateTextHeight() {
			super.updateTextHeight();
			GuiTextInput.this.textScaleUpdated();
		}
		
	}
	
}
