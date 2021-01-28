package io.msengine.client.renderer.font;

@Deprecated
public class FontHandlerGlyph {
	
	public final FontHandler handler;
	public final int x;
	public final int y;
	public final int width;
	public final float textureX;
	public final float textureY;
	public final float textureWidth;
	
	public FontHandlerGlyph(FontHandler handler, int x, int y, int width, float textureX, float textureY, float textureWidth) {
		
		this.handler = handler;
		this.x = x;
		this.y = y;
		this.width = width;
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		
	}
	
	public int getHeight() {
		return this.handler.getHeight();
	}
	
	public float getTextureHeight() {
		return this.handler.getTextureHeight();
	}
	
}