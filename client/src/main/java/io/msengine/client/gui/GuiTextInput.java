package io.msengine.client.gui;

import io.msengine.client.gui.event.GuiTextInputChangedEvent;
import io.msengine.client.renderer.gui.GUIMaskRectangle;
import io.msengine.client.renderer.gui.GuiMask;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowCharEventListener;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import io.msengine.client.renderer.window.listener.WindowMouseButtonEventListener;
import io.msengine.common.util.Color;
import io.sutil.ClipboardUtils;
import org.lwjgl.glfw.GLFW;

public class GuiTextInput extends GuiParent implements
		WindowCharEventListener,
		WindowKeyEventListener,
		WindowMouseButtonEventListener {
	
	public static final Color DEFAULT_CURSOR_COLOR = new Color(230, 230, 230, 0.8f);
	public static final Color DEFAULT_SELECTION_COLOR = new Color(105, 162, 255, 0.4f);
	
	private final InputText text;
	private final GuiColorSolid cursor;
	private final GuiColorSolid selection;
	
	private final GUIMaskRectangle mask;
	private final GuiMask[] maskArray;
	
	private final StringBuilder builder;
	private int cursorIndex = 0;
	private int cursorTick = 0;
	private int selectionIndex = 0;
	
	private float scrollPadding = 10;
	private int cursorAnimation = 10;
	
	private boolean active;
	
	public GuiTextInput() {
		
		this.text = new InputText();
		this.text.setAnchor(-1, 0);
		this.addChild(this.text);
		
		this.cursor = new GuiColorSolid(DEFAULT_CURSOR_COLOR);
		this.cursor.setAnchor(-1, 0);
		this.cursor.setVisible(false);
		this.addChild(this.cursor);
		
		this.selection = new GuiColorSolid(DEFAULT_SELECTION_COLOR);
		this.selection.setAnchor(-1, 0);
		this.selection.setVisible(false);
		this.addChild(this.selection);
		
		this.mask = new GUIMaskRectangle();
		this.maskArray = new GuiMask[] { this.mask };
		
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
		Window.getInstance().addMouseButtonEventListener(this);
		
	}
	
	@Override
	public void stop() {
		
		Window.getInstance().removeCharEventListener(this);
		Window.getInstance().removeKeyEventListener(this);
		Window.getInstance().removeMouseButtonEventListener(this);
		
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
		
		if (this.active) {
			if (this.cursorTick++ > this.cursorAnimation) {
				
				this.cursorTick = 0;
				this.cursor.setVisible(!this.cursor.isVisible());
				
			}
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
	public void setHeight(float height) {
		
		super.setHeight(height);
		
		this.text.setYPos(height / 2f);
		this.cursor.setYPos(height / 2f);
		this.selection.setYPos(height / 2f);
		
	}
	
	// Scroll padding //
	public float getScrollPadding() {
		return scrollPadding;
	}
	
	public void setScrollPadding(float scrollPadding) {
		this.scrollPadding = scrollPadding;
		this.updateCursor();
	}
	
	// Cursor animation //
	public int getCursorAnimation() {
		return cursorAnimation;
	}
	
	public void setCursorAnimation(int cursorAnimation) {
		this.cursorAnimation = cursorAnimation;
	}
	
	// Active //
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		
		if (this.active != active) {
			
			this.active = active;
			
			if (this.active) {
				
				this.cursorTick = 0;
				this.cursor.setVisible(true);
				this.setCursorPosition(0, true, false);
				
			} else {
				
				this.cursor.setVisible(false);
				this.selection.setVisible(false);
				
			}
			
		}
		
	}
	
	// Objects //
	public GuiTextColorable getText() {
		return this.text;
	}
	
	public GuiColorSolid getCursor() {
		return this.cursor;
	}
	
	public GuiColorSolid getSelection() {
		return this.selection;
	}
	
	// Private utility methods //
	private void textScaleUpdated() {
		
		if (this.cursor != null) {
			
			float textScale = this.text.getTextScale();
			
			this.cursor.setSize(textScale, this.text.getHeight());
			this.selection.setHeight(this.text.getHeight());
			this.text.setXPos(textScale);
			
		}
		
	}
	
	private void updateCursor() {
		
		float scrollPadding = this.scrollPadding;
		float charOffset = this.text.getCharOffset(this.cursorIndex - 1);
		float textScale = this.text.getTextScale();
		
		if (this.text.getWidth() <= (this.width - scrollPadding * 2)) {
			this.text.setXPos(scrollPadding);
		} else {
			
			float realOffset = charOffset + this.text.getXPos();
			
			if (realOffset < scrollPadding) {
				this.text.setXPos(scrollPadding - charOffset);
			} else if (realOffset > (this.width - scrollPadding - textScale)) {
				this.text.setXPos(this.width - scrollPadding - charOffset - textScale);
			}
			
		}
		
		this.cursor.setXPos(charOffset + this.text.getXPos());
		
		if (this.cursorIndex == this.selectionIndex) {
			this.selection.setVisible(false);
		} else {
			
			this.selection.setVisible(true);
			
			float selectionCharOffset = this.text.getCharOffset(this.selectionIndex - 1);
			float beginOffset = Math.min(charOffset, selectionCharOffset);
			float selectionWidth = Math.abs(charOffset - selectionCharOffset) + textScale;
			
			this.selection.setXPos(beginOffset + this.text.getXPos());
			this.selection.setWidth(selectionWidth);
			
		}
		
	}
	
	private void updateText() {
		
		String str = this.builder.toString();
		this.text.setText(str);
		this.fireEvent(new GuiTextInputChangedEvent(str));
		
	}
	
	private void setCursorPosition(int index, boolean updateCursor, boolean updateSelection) {
		
		if (this.cursorIndex != index) {
			
			this.cursorIndex = index;
			this.cursorTick = 0;
			
			if (!updateSelection)
				this.resetSelectionToCursor(false);
			
			if (updateCursor)
				this.updateCursor();
			
			if (!this.cursor.isVisible())
				this.cursor.setVisible(true);
			
		}
		
	}
	
	private void resetSelectionToCursor(boolean updateCursor) {
		
		this.selectionIndex = this.cursorIndex;
		
		if (updateCursor)
			this.updateCursor();
		
	}
	
	private boolean deleteSelection() {
	
		if (this.selectionIndex != this.cursorIndex) {
			
			if (this.cursorIndex < this.selectionIndex) {
				
				this.builder.delete(this.cursorIndex, this.selectionIndex);
				this.selectionIndex = this.cursorIndex;
				
			} else {
				
				this.builder.delete(this.selectionIndex, this.cursorIndex);
				this.cursorIndex = this.selectionIndex;
				
			}
			
			this.updateText();
			return true;
			
		} else {
			return false;
		}
		
	}
	
	public void selectAll() {
		
		this.setCursorPosition(0, false, false);
		this.setCursorPosition(this.builder.length(), true, true);
		
	}
	
	public void selectionToClipboard() {
	
		String str;
		
		if (this.cursorIndex < this.selectionIndex) {
			str = this.builder.substring(this.cursorIndex, this.selectionIndex);
		} else {
			str = this.builder.substring(this.selectionIndex, this.cursorIndex);
		}
		
		ClipboardUtils.setClipboardString(str);
	
	}
	
	@Override
	public void windowCharEvent(char codepoint) {
	
		if (this.renderable() && this.active) {
			
			this.deleteSelection();
			
			this.builder.insert(this.cursorIndex, codepoint);
			this.setCursorPosition(this.cursorIndex + 1, false, false);
			this.updateText();
			
		}
		
	}
	
	@Override
	public void windowKeyEvent(int key, int scancode, int action, int mods) {
	
		if (this.active) {
			
			if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
				
				boolean shifting = Window.isModActive(mods, GLFW.GLFW_MOD_SHIFT);
				
				if (key == GLFW.GLFW_KEY_LEFT) { // Move Cursor Left
					
					if (this.cursorIndex > 0) {
						this.setCursorPosition(this.cursorIndex - 1, true, shifting);
					} else if (!shifting) {
						this.resetSelectionToCursor(true);
					}
					
				} else if (key == GLFW.GLFW_KEY_RIGHT) { // Move Cursor Right
					
					if (this.cursorIndex < this.builder.length()) {
						this.setCursorPosition(this.cursorIndex + 1, true, shifting);
					} else if (!shifting) {
						this.resetSelectionToCursor(true);
					}
					
				} else if (key == GLFW.GLFW_KEY_HOME) { // Move Cursor at Start
					
					if (this.cursorIndex != 0) {
						this.setCursorPosition(0, true, shifting);
					} else if (!shifting) {
						this.resetSelectionToCursor(true);
					}
					
				} else if (key == GLFW.GLFW_KEY_END) { // Move Cursor at End
					
					int end = this.builder.length();
					
					if (this.cursorIndex != end) {
						this.setCursorPosition(end, true, shifting);
					} else if (!shifting) {
						this.resetSelectionToCursor(true);
					}
					
				} else if (key == GLFW.GLFW_KEY_BACKSPACE) { // Remove on left
					
					if (!this.deleteSelection()) {
						
						if (this.cursorIndex > 0) {
							
							this.builder.deleteCharAt(this.cursorIndex - 1);
							this.setCursorPosition(this.cursorIndex - 1, false, false);
							this.updateText();
							
						} else {
							this.resetSelectionToCursor(true);
						}
						
					}
					
				} else if (key == GLFW.GLFW_KEY_DELETE) { // Delete on right
					
					if (!this.deleteSelection()) {
						
						if (this.cursorIndex != this.builder.length()) {
							
							this.builder.deleteCharAt(this.cursorIndex);
							this.updateText();
							
						}
						
						this.resetSelectionToCursor(true);
						
					}
					
				} else if (key == GLFW.GLFW_KEY_V && Window.isModActive(mods, GLFW.GLFW_MOD_CONTROL)) { // Paste from clipboard
					
					this.deleteSelection();
					
					String clipboard = ClipboardUtils.getClipboardString();
					this.builder.insert(this.cursorIndex, clipboard);
					
					this.setCursorPosition(this.cursorIndex + clipboard.length(), false, false);
					this.updateText();
					
				} else if (key == GLFW.GLFW_KEY_Q && Window.isModActive(mods, GLFW.GLFW_MOD_CONTROL)) { // Selection whole input
					
					this.selectAll();
					
				} else if (key == GLFW.GLFW_KEY_C && Window.isModActive(mods, GLFW.GLFW_MOD_CONTROL)) { // Copy to clipboard
					
					this.selectionToClipboard();
					
				} else if (key == GLFW.GLFW_KEY_ESCAPE) {
					this.setActive(false);
				}
				
			}
			
		}
	
	}
	
	@Override
	public void windowMouseButtonEvent(int button, int action, int mods) {
	
		Window win = Window.getInstance();
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
			this.setActive(this.isPointOver(win.getCursorPosX(), win.getCursorPosY()));
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
