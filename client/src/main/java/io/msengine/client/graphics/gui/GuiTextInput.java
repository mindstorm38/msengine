package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.event.GuiTextInputChangedEvent;
import io.msengine.client.window.Window;
import io.msengine.client.window.listener.WindowCharEventListener;
import io.msengine.client.window.listener.WindowKeyEventListener;
import io.msengine.client.window.listener.WindowMouseButtonEventListener;
import io.msengine.common.util.Color;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GuiTextInput extends GuiParent implements
		WindowCharEventListener,
		WindowKeyEventListener,
		WindowMouseButtonEventListener {
	
	public static final Color DEFAULT_CURSOR_COLOR = new Color(230, 230, 230, 0.8f);
	public static final Color DEFAULT_SELECTION_COLOR = new Color(105, 162, 255, 0.4f);
	
	private final InnerText text;
	private final GuiColorSolid cursor;
	private final GuiColorSolid selection;
	
	private final StringBuilder builder = new StringBuilder();
	
	private int cursorIndex;
	private int cursorTick;
	private int cursorBlinkDelay = 10;
	private float cursorWidth = 1;
	
	private int selectionIndex;
	private float scrollPadding = 10;
	
	private boolean active;
	
	public GuiTextInput() {
		
		this.text = new InnerText();
		this.text.setAnchor(-1, 0);
		this.addChild(this.text);
		
		this.cursor = new GuiColorSolid(DEFAULT_CURSOR_COLOR);
		this.cursor.setVisible(false);
		this.cursor.setAnchor(0, 0);
		this.addChild(this.cursor);
		
		this.selection = new GuiColorSolid(DEFAULT_SELECTION_COLOR);
		this.selection.setVisible(false);
		this.selection.setAnchor(-1, 0);
		this.addChild(this.selection);
		
		this.updateCursor();
		
	}
	
	@Override
	protected void init() {
		super.init();
		this.getWindowEventManager().addEventListener(WindowCharEventListener.class, this);
		this.getWindowEventManager().addEventListener(WindowKeyEventListener.class, this);
		this.getWindowEventManager().addEventListener(WindowMouseButtonEventListener.class, this);
	}
	
	@Override
	protected void stop() {
		this.getWindowEventManager().removeEventListener(WindowCharEventListener.class, this);
		this.getWindowEventManager().removeEventListener(WindowKeyEventListener.class, this);
		this.getWindowEventManager().removeEventListener(WindowMouseButtonEventListener.class, this);
		super.stop();
	}
	
	@Override
	protected void render(float alpha) {
		super.render(alpha);
	}
	
	@Override
	protected void update() {
		super.update();
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.text.setYPos(this.realHeight / 2f);
		this.cursor.setYPos(this.realHeight / 2f);
		this.selection.setYPos(this.realHeight / 2f);
	}
	
	@Override
	public void onWindowCharEvent(Window origin, int codePoint) {
		if (this.mustRender() && this.active) {
			this.deleteSelection(false);
			insertBuilderCodePoint(this.builder, this.cursorIndex, codePoint);
			this.setCursorPosition(this.cursorIndex + 1, false, false);
			this.updateText();
		}
	}
	
	@Override
	public void onWindowKeyEvent(Window origin, int key, int scanCode, int action, int mods) {
		if (this.active) {
			if (action == Window.ACTION_PRESS || action == Window.ACTION_REPEAT) {
				this.handleKeyEvent(key, mods);
			}
		}
	}
	
	@Override
	public void onWindowMouseButtonEvent(Window origin, int button, int action, int mods) {
		if (button == Window.MOUSE_BUTTON_LEFT && action == Window.ACTION_PRESS) {
			origin.getCursorPos((x, y) -> {
				//System.out.println("Mouse clicked at: " + x + "/" + y);
				this.setActive(this.isPointOver((float) x, (float) y));
			});
		}
	}
	
	// Active //
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		if (this.active != active) {
			//System.out.println("New active: " + active);
			this.active = active;
			if (active) {
				this.cursorTick = 0;
				this.cursor.setVisible(true);
				this.setCursorPosition(this.builder.length(), true, false);
			} else {
				this.cursor.setVisible(false);
				this.selection.setVisible(false);
			}
		}
	}
	
	// Access children //
	
	public GuiText getText() {
		return this.text;
	}
	
	public GuiColorSolid getCursor() {
		return this.cursor;
	}
	
	public GuiColorSolid getSelection() {
		return this.selection;
	}
	
	// Text //
	
	public String getInputText() {
		return this.builder.toString();
	}
	
	public void setInputText(String text) {
		this.builder.delete(0, this.builder.length());
		this.builder.append(text);
		this.text.setText(text);
		this.fireEvent(new GuiTextInputChangedEvent(text));
	}
	
	public void updateText() {
		String text = this.getInputText();
		this.text.setText(text);
		this.fireEvent(new GuiTextInputChangedEvent(text));
	}
	
	// Internal utilities //
	
	protected void onTextSizeChanged() {
		float textHeight = this.text.getRealHeight();
		this.cursor.setSize(this.cursorWidth, textHeight);
		this.selection.setHeight(textHeight);
	}
	
	protected void updateCursor() {
		
		float scrollPadding = this.scrollPadding;
		float cursorOffset = this.text.getCodePointOffset(this.cursorIndex - 1);
		float cursorWidth = this.cursorWidth;
		float width = this.realWidth;
		float textPos = this.text.getXPos();
		
		if (this.text.getRealWidth() <= (width - cursorWidth * 2)) {
			textPos = cursorWidth;
			this.text.setXPos(textPos);
		} else {
			float realOffset = cursorOffset + textPos;
			float textWidth = this.text.getRealWidth();
			if (realOffset < scrollPadding) {
				float newPos = -cursorOffset + scrollPadding;
				if (textPos < newPos) {
					textPos = Math.min(newPos, cursorWidth);
					this.text.setXPos(textPos);
				}
			} else if (realOffset > (width - scrollPadding - cursorWidth)) {
				float newPos = width - cursorOffset - cursorWidth - scrollPadding;
				if (textPos > newPos) {
					textPos = Math.max(newPos, width - textWidth - cursorWidth);
					this.text.setXPos(textPos);
				}
			} else {
				float newPos = width - textWidth - cursorWidth;
				if (textPos < newPos) {
					textPos = newPos;
					this.text.setXPos(textPos);
				}
			}
		}
		
		this.cursor.setXPos(cursorOffset + textPos);
		
		if (this.cursorIndex == this.selectionIndex) {
			this.selection.setVisible(false);
		} else {
			
			this.selection.setVisible(true);
			
			float selectionOffset = this.text.getCodePointOffset(this.selectionIndex - 1);
			float beginOffset = Math.min(cursorOffset, selectionOffset);
			float selectionWidth = Math.abs(cursorOffset - selectionOffset) + cursorWidth;
			
			this.selection.setXPos(beginOffset + textPos);
			this.selection.setWidth(selectionWidth);
			
		}
		
	}
	
	protected void moveCursorTo(boolean cond, int index, boolean shift) {
		if (cond) {
			this.setCursorPosition(index, true, shift);
		} else if (!shift) {
			this.resetSelectionToCursor(true);
		}
	}
	
	protected void handleKeyEvent(int key, int mods) {
		
		boolean shift = Window.hasModifier(mods, Window.MOD_SHIFT);
		boolean ctrl = Window.hasModifier(mods, Window.MOD_CTRL);
		
		if (key == GLFW.GLFW_KEY_LEFT) {
			// Move Cursor Left
			this.moveCursorTo(this.cursorIndex > 0, this.cursorIndex - 1, shift);
		} else if (key == GLFW.GLFW_KEY_RIGHT) {
			// Move Cursor Right
			this.moveCursorTo(this.cursorIndex < this.builder.length(), this.cursorIndex + 1, shift);
		} else if (key == GLFW.GLFW_KEY_HOME) {
			// Move Cursor at Start
			this.moveCursorTo(this.cursorIndex != 0, 0, shift);
		} else if (key == GLFW.GLFW_KEY_END) {
			// Move Cursor at End
			int end = this.builder.length();
			this.moveCursorTo(this.cursorIndex != end, end, shift);
		} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
			// Remove on left
			if (!this.deleteSelection(true)) {
				if (this.cursorIndex > 0) {
					this.builder.deleteCharAt(this.cursorIndex - 1);
					this.setCursorPosition(this.cursorIndex - 1, false, false);
					this.updateText();
				} else {
					this.resetSelectionToCursor(true);
				}
			}
		} else if (key == GLFW.GLFW_KEY_DELETE) {
			// Delete on right
			if (!this.deleteSelection(true)) {
				if (this.cursorIndex < this.builder.length()) {
					this.builder.deleteCharAt(this.cursorIndex);
					this.updateText();
				}
				this.resetSelectionToCursor(true);
			}
		} else if (key == GLFW.GLFW_KEY_V && ctrl) {
			// Paste from clipboard
			this.deleteSelection(false);
			String clipboard = getClipboardString();
			if (!clipboard.isEmpty()) {
				this.builder.insert(this.cursorIndex, clipboard);
				this.setCursorPosition(this.cursorIndex + clipboard.length(), false, false);
			}
			this.updateText();
		} else if (key == GLFW.GLFW_KEY_Q && ctrl) {
			// Select whole input
			this.selectAll();
		} else if (key == GLFW.GLFW_KEY_C && ctrl) {
			this.copySelectionToClipboard();
		} else if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.setActive(false);
		}
		
	}
	
	public void setCursorPosition(int index, boolean updateCursor, boolean select) {
		if (this.cursorIndex != index) {
			this.cursorIndex = index;
			this.cursorTick = 0;
			if (!select) {
				this.resetSelectionToCursor(false);
			}
			if (updateCursor) {
				this.updateCursor();
			}
			if (!this.cursor.isVisible()) {
				this.cursor.setVisible(true);
			}
		}
	}
	
	private void resetSelectionToCursor(boolean updateCursor) {
		this.selectionIndex = this.cursorIndex;
		if (updateCursor) {
			this.updateCursor();
		}
	}
	
	private boolean deleteSelection(boolean updateText) {
		if (this.selectionIndex != this.cursorIndex) {
			if (this.cursorIndex < this.selectionIndex) {
				this.builder.delete(this.cursorIndex, this.selectionIndex);
			} else {
				this.builder.delete(this.selectionIndex, this.cursorIndex);
			}
			if (updateText) {
				this.updateText();
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void selectAll() {
		this.setCursorPosition(0, false, false);
		this.setCursorPosition(this.builder.length(), true, true);
	}
	
	public void copySelectionToClipboard() {
		if (this.cursorIndex < this.selectionIndex) {
			setClipboardString(this.builder.substring(this.cursorIndex, this.selectionIndex));
		} else {
			setClipboardString(this.builder.substring(this.selectionIndex, this.cursorIndex));
		}
	}
	
	// Internal classes //
	
	private class InnerText extends GuiText {
		
		@Override
		protected void onTextBuffersRecomputed() {
			super.onTextBuffersRecomputed();
			GuiTextInput.this.updateCursor();
		}
		
		@Override
		protected void onFontChanged() {
			super.onFontChanged();
			GuiTextInput.this.onTextSizeChanged();
		}
		
	}
	
	// Utils //
	
	public static String getClipboardString() {
		try {
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
			}
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void setClipboardString(String str) {
		StringSelection selection = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
	
	public static void insertBuilderCodePoint(StringBuilder builder, int index, int codePoint) {
		if (Character.isBmpCodePoint(codePoint)) {
			builder.insert(index, (char) codePoint);
		} else if (Character.isValidCodePoint(codePoint)) {
			// Insert in reverse order (low before high)
			builder.insert(index, Character.lowSurrogate(codePoint));
			builder.insert(index, Character.highSurrogate(codePoint));
		}
	}
	
}
