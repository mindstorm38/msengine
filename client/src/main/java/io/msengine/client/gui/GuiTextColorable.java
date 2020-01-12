package io.msengine.client.gui;

import io.msengine.client.renderer.font.FontHandler;
import io.msengine.common.util.Color;

/**
 *
 * A text object that can be colored (one color for whole text).
 *
 * @author Theo Rozier
 *
 */
public class GuiTextColorable extends GuiTextBase {
	
	private final Color textColor = Color.WHITE.copy();
	
	public GuiTextColorable(FontHandler font, String text) {
		super(font, text);
	}
	
	public GuiTextColorable(FontHandler font) {
		super(font);
	}
	
	public GuiTextColorable(String text) {
		super(text);
	}
	
	public GuiTextColorable() {
		super();
	}
	
	@Override
	public void render(float alpha) {
		
		this.renderer.setGlobalColor(this.textColor);
		super.render(alpha);
		this.renderer.resetGlobalColor();
		
	}
	
	/**
	 * @return Immutable internal text color, you can modify its components.
	 */
	public Color getTextColor() {
		return this.textColor;
	}
	
	public void setTextColor(Color color) {
		this.textColor.setAll(color);
	}
	
	public void setTextColor(float r, float g, float b) {
		this.textColor.setAll(r, g, b);
	}
	
	public void setTextColor(int r, int g, int b) {
		this.textColor.setAll(r, g, b);
	}
	
}
