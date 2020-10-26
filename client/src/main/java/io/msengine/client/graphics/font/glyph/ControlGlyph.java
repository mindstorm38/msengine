package io.msengine.client.graphics.font.glyph;

public class ControlGlyph {
	
	protected final int codePoint;
	private final float advance;
	
	public ControlGlyph(int codePoint, float advance) {
		this.codePoint = codePoint;
		this.advance = advance;
	}
	
}
