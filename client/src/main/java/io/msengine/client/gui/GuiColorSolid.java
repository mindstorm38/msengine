package io.msengine.client.gui;

import io.msengine.common.util.Color;

public class GuiColorSolid extends GuiColorBase {
	
	private final Color color = Color.WHITE.copy();
	
	public GuiColorSolid(Color color) {
		this.setColor(color);
	}
	
	public GuiColorSolid() { }
	
	@Override
	public Color getCornerColor(int corner) {
		return this.color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color.setAll(color);
		this.updateColors();
	}
	
	public void setColor(int r, int g, int b) {
		this.color.setAll(r, g, b);
		this.updateColors();
	}
	
	public void setColor(int r, int g, int b, float a) {
		this.color.setAll(r, g, b);
		this.color.setAlpha(a);
		this.updateColors();
	}
	
}
