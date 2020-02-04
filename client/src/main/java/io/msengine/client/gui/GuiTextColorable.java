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
	
	protected final Color textColor = Color.WHITE.copy();
	
	protected final Color shadowColor = Color.BLACK.copy();
	protected float shadowOffsetX;
	protected float shadowOffsetY;
	protected boolean shadowReady;
	
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
	public void renderText(float alpha) {
		
		this.renderer.setTextureSampler(this.font);
		
		if (this.shadowReady) {
			
			this.renderer.setGlobalColor(this.shadowColor);
			
			this.model.push().translate(this.xIntOffset + this.shadowOffsetX, this.yIntOffset + this.shadowOffsetY).apply();
			this.buffer.drawElements();
			this.model.pop();
			
		}
		
		this.renderer.setGlobalColor(this.textColor);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
		this.buffer.drawElements();
		this.model.pop();
		
		this.renderer.resetGlobalColor();
		
		this.renderer.resetTextureSampler();
		
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
	
	public void setTextColor(int r, int g, int b) {
		this.textColor.setAll(r, g, b);
	}
	
	/**
	 * @return Immutable internal shadow color, you can modify its components.
	 */
	public Color getShadowColor() {
		return this.shadowColor;
	}
	
	public void setShadowColor(Color color) {
		this.shadowColor.setAll(color);
	}
	
	public void setShadowColor(int r, int g, int b) {
		this.shadowColor.setAll(r, g, b);
	}
	
	public float getShadowOffsetX() {
		return shadowOffsetX;
	}
	
	public void setShadowOffsetX(float shadowOffsetX) {
		this.shadowOffsetX = shadowOffsetX;
		this.updateShadowReady();
	}
	
	public float getShadowOffsetY() {
		return shadowOffsetY;
	}
	
	public void setShadowOffsetY(float shadowOffsetY) {
		this.shadowOffsetY = shadowOffsetY;
		this.updateShadowReady();
	}
	
	public void setShadowOffset(float xOff, float yOff) {
		this.setShadowOffsetX(xOff);
		this.setShadowOffsetY(yOff);
	}
	
	private void updateShadowReady() {
		this.shadowReady = this.shadowOffsetX != 0 || this.shadowOffsetY != 0;
	}
	
}
