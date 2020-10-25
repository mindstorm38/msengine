package io.msengine.client.graphics.font;

public class Glyph {

	private final float x, y;
	private final float w, h;
	
	public Glyph(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getW() {
		return this.w;
	}
	
	public float getH() {
		return this.h;
	}

}
