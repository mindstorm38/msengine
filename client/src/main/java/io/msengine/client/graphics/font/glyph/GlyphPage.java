package io.msengine.client.graphics.font.glyph;

import io.msengine.client.graphics.font.FontTexture2D;

public class GlyphPage {

	private final FontTexture2D texture;
	private final int refCodePoint;
	private final Glyph[] glyphs;
	
	public GlyphPage(FontTexture2D texture, int refCodePoint, Glyph[] glyphs) {
		this.texture = texture;
		this.refCodePoint = refCodePoint;
		this.glyphs = glyphs;
	}
	
	public final FontTexture2D getTexture() {
		return this.texture;
	}
	
	public final int getRefCodePoint() {
		return this.refCodePoint;
	}
	
	public final int getLastCodePoint() {
		return this.refCodePoint + this.glyphs.length - 1;
	}
	
	public final int getGlyphsCount() {
		return this.glyphs.length;
	}
	
	public final boolean hasCodePoint(int codePoint) {
		int off = codePoint - this.refCodePoint;
		return off >= 0 && off < this.glyphs.length;
	}
	
	public Glyph getGlyph(int codePoint) {
		int off = codePoint - this.refCodePoint;
		if (off < 0 || off >= this.glyphs.length) {
			throw new IndexOutOfBoundsException("Invalid code point for this glyph page.");
		} else {
			return this.glyphs[off];
		}
	}
	
	public final int getTextureName() {
		return this.texture.getName();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<from=" + this.getRefCodePoint() + ", to=" + this.getLastCodePoint() + ", tex=" + this.texture + ">";
	}
	
}
