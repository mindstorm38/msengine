package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.gui.event.GuiEvent;
import io.msengine.client.graphics.gui.event.GuiEventListener;
import io.msengine.client.graphics.gui.mask.GuiMask;
import io.msengine.client.graphics.gui.mask.GuiMaskRect;
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
import java.util.function.Supplier;

public class GuiTextInput extends GuiParent implements
		WindowCharEventListener,
		WindowKeyEventListener,
		WindowMouseButtonEventListener {
	
	public static final Color DEFAULT_CURSOR_COLOR = new Color(230, 230, 230, 0.8f);
	public static final Color DEFAULT_SELECTION_COLOR = new Color(105, 162, 255, 0.4f);
	public static final Color DEFAULT_PLACEHOLDER_COLOR = new Color(105, 105, 105, 0.8f);
	
	private final InnerText text;
	private GuiText placeholder;
	private final GuiColorSolid cursor;
	private final GuiColorSolid selection;
	private final GuiMaskRect mask;
	
	private final StringBuilder builder = new StringBuilder();
	
	private int cursorIndex;
	
	// In ms
	private long cursorLastBlink;
	private int cursorBlinkDelay = 500;
	
	private int selectionIndex;
	private float scrollPadding = 10;
	
	private boolean active;
	
	public GuiTextInput() {
		
		this.cursor = new GuiColorSolid(DEFAULT_CURSOR_COLOR);
		this.cursor.setVisible(false);
		this.cursor.setAnchor(0, 0);
		this.cursor.setYSupAnchor(0);
		this.cursor.setWidth(1);
		this.addChild(this.cursor);
		
		this.selection = new GuiColorSolid(DEFAULT_SELECTION_COLOR);
		this.selection.setVisible(false);
		this.selection.setYAnchor(0);
		this.selection.setYSupAnchor(0);
		this.addChild(this.selection);
		
		this.mask = new GuiMaskRect();
		this.addChild(this.mask);
		
		// Adding text after all to avoid NPE
		// if other children are null in real
		// size callbacks.
		this.text = new InnerText();
		this.addChild(this.text, this.mask);
		this.text.setYAnchor(0);
		this.text.setYSupAnchor(0);
		
		this.updateCursor();
		
	}
	
	@Override
	protected void init() {
		super.init();
		this.getWindow().addEventListener(WindowCharEventListener.class, this);
		this.getWindow().addEventListener(WindowKeyEventListener.class, this);
		this.getWindow().addEventListener(WindowMouseButtonEventListener.class, this);
	}
	
	@Override
	protected void stop() {
		this.getWindow().removeEventListener(WindowCharEventListener.class, this);
		this.getWindow().removeEventListener(WindowKeyEventListener.class, this);
		this.getWindow().removeEventListener(WindowMouseButtonEventListener.class, this);
		super.stop();
	}
	
	@Override
	protected void render(float alpha) {
		try (GuiMask.MaskTracker ignored = this.mask.mask()) {
			super.render(alpha);
		}
	}
	
	@Override
	protected void update() {
		
		super.update();
		
		if (this.active) {
			
			long lastBlink = this.cursorLastBlink;
			long current = System.currentTimeMillis();
			
			if (lastBlink == 0) {
				this.cursorLastBlink = current;
			} else if (current - lastBlink >= this.cursorBlinkDelay) {
				this.cursorLastBlink = current;
				this.cursor.setVisible(!this.cursor.isVisible());
			}
			
		}
		
	}
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.mask.setWidth(this.realWidth);
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.mask.setHeight(this.realHeight);
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
		if (this.mustRender() && this.active) {
			if (action == Window.ACTION_PRESS || action == Window.ACTION_REPEAT) {
				this.handleKeyEvent(key, mods);
			}
		}
	}
	
	@Override
	public void onWindowMouseButtonEvent(Window origin, int button, int action, int mods) {
		if (this.mustRender() && button == Window.MOUSE_BUTTON_LEFT && action == Window.ACTION_PRESS) {
			this.setActive(this.isCursorOver());
			/*origin.getCursorPos((x, y) -> {
				//System.out.println("Mouse clicked at: " + x + "/" + y);
				this.setActive(this.isPointOver((float) x, (float) y));
			});*/
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
				this.cursorLastBlink = 0;
				this.cursor.setVisible(true);
				this.setCursorPosition(this.builder.length(), true, false);
			} else {
				this.cursor.setVisible(false);
				this.selection.setVisible(false);
			}
			this.fireEvent(new ActiveEvent(active));
		}
	}
	
	// Properties //
	
	public int getCursorBlinkDelay() {
		return this.cursorBlinkDelay;
	}
	
	public void setCursorBlinkDelay(int delay) {
		this.cursorBlinkDelay = delay;
	}
	
	public float getScrollPadding() {
		return this.scrollPadding;
	}
	
	public void setScrollPadding(float padding) {
		if (padding != this.scrollPadding) {
			this.scrollPadding = padding;
			this.updateCursor();
		}
	}
	
	// Access children //
	
	public GuiText getText() {
		return this.text;
	}
	
	public GuiText getPlaceholder() {
		return this.placeholder;
	}
	
	public GuiColorSolid getCursor() {
		return this.cursor;
	}
	
	public GuiColorSolid getSelection() {
		return this.selection;
	}
	
	public void setTextFontFamily(Supplier<FontFamily> supplier) {
		this.text.setFontFamily(supplier);
		if (this.placeholder != null) {
			this.placeholder.setFontFamily(supplier);
		}
	}
	
	public void setTextFontFamily(FontFamily family) {
		this.text.setFontFamily(family);
		if (this.placeholder != null) {
			this.placeholder.setFontFamily(family);
		}
	}
	
	public void setTextFontSize(float size) {
		this.text.setFontSize(size);
		if (this.placeholder != null) {
			this.placeholder.setFontSize(size);
		}
	}
	
	public void setTextFont(Supplier<FontFamily> supplier, float size) {
		this.text.setFont(supplier, size);
		if (this.placeholder != null) {
			this.placeholder.setFont(supplier, size);
		}
	}
	
	public void setTextFont(FontFamily family, float size) {
		this.text.setFont(family, size);
		if (this.placeholder != null) {
			this.placeholder.setFont(family, size);
		}
	}
	
	public float getTextFontSize() {
		return this.text.getFontSize();
	}
	
	public FontFamily getTextFontFamily() {
		return this.text.getFontFamily();
	}
	
	@Deprecated
	public void setTextFont(Font font) {
		this.text.setFont(font);
	}
	
	// Placeholder //
	
	public String getPlaceholderText() {
		return this.placeholder == null ? "" : this.placeholder.getText();
	}
	
	public void setPlaceholderText(String placeholderText) {
		if (this.placeholder == null) {
			this.placeholder = new GuiText();
			this.placeholder.addColorEffect(0, DEFAULT_PLACEHOLDER_COLOR);
			this.placeholder.setYAnchor(0);
			this.placeholder.setYSupAnchor(0);
			this.placeholder.setYPos(this.text.getYPos());
			this.placeholder.setFont(this.text.fontSupplier, this.text.fontSize);
			this.updatePlaceholderVisible();
			this.addChild(this.placeholder, this.text);
		}
		this.placeholder.setText(placeholderText);
	}
	
	private void updatePlaceholderVisible() {
		if (this.placeholder != null) {
			this.placeholder.setVisible(this.getInputText().isEmpty());
		}
	}
	
	// Text //
	
	public String getInputText() {
		return this.builder.toString();
	}
	
	public void setInputText(String text) {
		this.builder.delete(0, this.builder.length());
		this.builder.append(text);
		this.text.setText(text);
		this.updatePlaceholderVisible();
		this.fireEvent(new ChangedEvent(text));
	}
	
	public void updateText() {
		String text = this.getInputText();
		this.text.setText(text);
		this.updatePlaceholderVisible();
		this.fireEvent(new ChangedEvent(text));
	}
	
	// Internal utilities //
	
	protected void onTextSizeChanged() {
		float textHeight = this.text.getRealHeight();
		this.cursor.setHeight(textHeight);
		this.selection.setHeight(textHeight);
	}
	
	protected void updateCursor() {
		
		float scrollPadding = this.scrollPadding;
		float cursorOffset = this.text.getCodePointOffset(this.cursorIndex - 1);
		float cursorWidth = this.cursor.getWidth();
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
			if (this.cursorIndex == this.selectionIndex || shift) {
				this.moveCursorTo(this.cursorIndex > 0, this.cursorIndex - 1, shift);
			} else if (this.cursorIndex < this.selectionIndex) {
				this.resetSelectionToCursor(true);
			} else {
				this.setCursorPosition(this.selectionIndex, true, false);
			}
		} else if (key == GLFW.GLFW_KEY_RIGHT) {
			// Move Cursor Right
			if (this.cursorIndex == this.selectionIndex || shift) {
				this.moveCursorTo(this.cursorIndex < this.builder.length(), this.cursorIndex + 1, shift);
			} else if (this.cursorIndex < this.selectionIndex) {
				this.setCursorPosition(this.selectionIndex, true, false);
			} else {
				this.resetSelectionToCursor(true);
			}
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
					this.resetCursorBlink();
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
		} else if (key == GLFW.GLFW_KEY_X && ctrl) {
			if (this.isSelecting()) {
				this.copySelectionToClipboard();
				this.deleteSelection(true);
			}
		} else if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.setActive(false);
		}
		
	}
	
	public void setCursorPosition(int index, boolean updateCursor, boolean select) {
		if (this.cursorIndex != index) {
			this.cursorIndex = index;
			if (!select) {
				this.resetSelectionToCursor(false);
			}
			if (updateCursor) {
				this.updateCursor();
			}
			this.resetCursorBlink();
		}
	}
	
	public void resetCursorBlink() {
		this.cursorLastBlink = 0;
		if (!this.cursor.isVisible()) {
			this.cursor.setVisible(true);
		}
	}
	
	public boolean isSelecting() {
		return this.selectionIndex != this.cursorIndex;
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
				this.resetSelectionToCursor(true);
			} else {
				this.builder.delete(this.selectionIndex, this.cursorIndex);
				this.setCursorPosition(this.selectionIndex, true, false);
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
		protected void onYPosChanged() {
			super.onYPosChanged();
			if (GuiTextInput.this.placeholder != null) {
				GuiTextInput.this.placeholder.setYPos(this.yPos);
			}
		}
		
		@Override
		protected void onTextBuffersRecomputed() {
			super.onTextBuffersRecomputed();
			GuiTextInput.this.updateCursor();
		}
		
		@Override
		protected void onRealHeightChanged() {
			super.onRealHeightChanged();
			GuiTextInput.this.onTextSizeChanged();
		}
		
	}
	
	// Event class //
	
	public void addChangedEventListener(GuiEventListener<? super ChangedEvent> listener) {
		this.addEventListener(ChangedEvent.class, listener);
	}
	
	public void addActiveEventListener(GuiEventListener<? super ActiveEvent> listener) {
		this.addEventListener(ActiveEvent.class, listener);
	}
	
	public static class ChangedEvent extends GuiEvent {
		
		private final String value;
		
		public ChangedEvent(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
		
	}
	
	public static class ActiveEvent extends GuiEvent {
		
		private final boolean active;
		
		public ActiveEvent(boolean active) {
			this.active = active;
		}
		
		public boolean isActive() {
			return this.active;
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
