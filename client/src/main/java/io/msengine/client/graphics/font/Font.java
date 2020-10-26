package io.msengine.client.graphics.font;

import io.msengine.client.graphics.font.glyph.GlyphPage;

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
	
	public boolean isValid() {
		return this.family.isValid();
	}
	
	protected void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("This font is no longer valid (check its family).");
		}
	}
	
	public abstract GlyphPage getGlyphPage(int codePoint);
	
	@Override
	public abstract void close();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<size=" + this.size + ">";
	}
	
}
