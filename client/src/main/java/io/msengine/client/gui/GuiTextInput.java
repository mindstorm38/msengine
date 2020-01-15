package io.msengine.client.gui;

import io.msengine.client.renderer.gui.GUIMaskRectangle;
import io.msengine.client.renderer.gui.GuiMask;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowCharEventListener;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import org.lwjgl.glfw.GLFW;

public class GuiTextInput extends GuiParent implements
		WindowCharEventListener,
		WindowKeyEventListener {
	
	private final InputText text;
	private final GuiColorSolid cursor;
	
	private final GUIMaskRectangle mask;
	private final GuiMask[] maskArray;
	
	private final StringBuilder builder;
	private int cursorIndex = 0;
	
	public GuiTextInput() {
		
		this.text = new InputText();
		this.text.setAnchor(-1, -1);
		this.addChild(this.text);
		
		this.cursor = new GuiColorSolid();
		this.cursor.setAnchor(0, -1);
		this.addChild(this.cursor);
		
		this.mask = new GUIMaskRectangle();
		this.maskArray = new GuiMask[] { this.mask };
		
		this.builder = new StringBuilder();
		
		this.updateCursorPosition();
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
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
	}
	
	public GuiTextColorable getText() {
		return this.text;
	}
	
	public GuiColorSolid getCursor() {
		return this.cursor;
	}
	
	private void textScaleUpdated() {
		
		if (this.cursor != null) {
			this.cursor.setSize(this.text.getTextScale(), this.text.getHeight());
		}
		
	}
	
	private void updateCursorPosition() {
		this.cursor.setXPos(this.text.getCharOffset(this.cursorIndex - 1) + (this.text.getCharSpacing() / 2f));
	}
	
	private void updateText() {
		this.text.setText(this.builder.toString());
	}
	
	@Override
	public void windowCharEvent(char codepoint) {
	
		if (this.renderable()) {
			
			this.builder.append(codepoint);
			this.cursorIndex++;
			
			this.updateText();
			
		}
		
	}
	
	@Override
	public void windowKeyEvent(int key, int scancode, int action, int mods) {
	
		if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
			
			if (key == GLFW.GLFW_KEY_LEFT) { // Move Cursor Left
				
				if (this.cursorIndex > 0) {
					
					this.cursorIndex--;
					this.updateCursorPosition();
					
				}
				
			} else if (key == GLFW.GLFW_KEY_RIGHT) { // Move Cursor Right
				
				if (this.cursorIndex < this.builder.length()) {
					
					this.cursorIndex++;
					this.updateCursorPosition();
					
				}
			
			}else if (key == GLFW.GLFW_KEY_HOME) {
				
				if (this.cursorIndex != 0) {
					
					this.cursorIndex = 0;
					this.updateCursorPosition();
					
				}
				
			} else if (key == GLFW.GLFW_KEY_END) {
				
				int end = this.builder.length();
				
				if (this.cursorIndex != end) {
					
					this.cursorIndex = end;
					this.updateCursorPosition();
					
				}
				
			}  else if (key == GLFW.GLFW_KEY_BACKSPACE) {
				
				if (this.cursorIndex > 0) {
					
					this.builder.deleteCharAt(this.cursorIndex - 1);
					this.cursorIndex--;
					
					this.updateText();
					
				}
				
			} else if (key == GLFW.GLFW_KEY_DELETE) {
				
				if (this.cursorIndex != this.builder.length()) {
					
					this.builder.deleteCharAt(this.cursorIndex);
					this.updateText();
					
				}
				
			}
			
		}
	
	}
	
	private class InputText extends GuiTextColorable {
		
		@Override
		protected void updateTextBuffers() {
			super.updateTextBuffers();
			GuiTextInput.this.updateCursorPosition();
		}
		
		@Override
		public void updateTextHeight() {
			super.updateTextHeight();
			GuiTextInput.this.textScaleUpdated();
		}
		
	}
	
}
