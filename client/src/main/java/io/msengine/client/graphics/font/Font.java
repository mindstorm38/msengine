package io.msengine.client.graphics.font;

import java.util.Objects;

public abstract class Font implements AutoCloseable {

	private final FontFamily family;
	private final float size;
	
	public Font(FontFamily family, float size) {
		this.family = Objects.requireNonNull(family);
		this.size = size;
	}
	
	public FontFamily getFamily() {
		return this.family;
	}
	
	public float getSize() {
		return this.size;
	}
	
	public abstract GlyphPage getGlyphPage(int codePoint);
	
	public Glyph getGlyph(int codePoint) {
		GlyphPage page = this.getGlyphPage(codePoint);
		return page == null ? null : page.getGlyph(codePoint);
	}
	
	@Override
	public abstract void close();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<size=" + this.size + ">";
	}
	
}
