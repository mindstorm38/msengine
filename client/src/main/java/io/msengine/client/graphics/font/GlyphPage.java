package io.msengine.client.graphics.font;

public class GlyphPage {

	private final int textureName;
	private final int refCodePoint;
	private final Glyph[] glyphs;
	
	public GlyphPage(int textureName, int refCodePoint, Glyph[] glyphs) {
		this.textureName = textureName;
		this.refCodePoint = refCodePoint;
		this.glyphs = glyphs;
	}
	
	public final int getTextureName() {
		return this.textureName;
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
	
	@Override
	public String toString() {
		return "GlyphPage<from=" + this.getRefCodePoint() + ", to=" + this.getLastCodePoint() + ", tex=" + this.textureName + ">";
	}
	
}
