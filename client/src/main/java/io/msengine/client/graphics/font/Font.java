package io.msengine.client.graphics.font;

import io.msengine.client.graphics.font.glyph.GlyphPage;

import java.util.Objects;

public abstract class Font implements AutoCloseable {

	private final FontFamily family;
	private final float size;
	private final float ascent;
	private final float descent;
	
	public Font(FontFamily family, float size, float ascent, float descent) {
		this.family = Objects.requireNonNull(family);
		this.size = size;
		this.ascent = ascent;
		this.descent = descent;
	}
	
	public FontFamily getFamily() {
		return this.family;
	}
	
	/**
	 * @return Font height (in pixels).
	 */
	public float getSize() {
		return this.size;
	}
	
	/**
	 * @return The font vertical ascent (pixels above the font base line).
	 */
	public float getAscent() {
		return this.ascent;
	}
	
	/**
	 * @return The font vertical descent (pixels below the font base line).
	 */
	public float getDescent() {
		return this.descent;
	}
	
	public boolean isValid() {
		return this.family.isValid();
	}
	
	protected void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("This font is no longer valid (check its family).");
		}
	}
	
	public abstract boolean isScalable();
	public abstract GlyphPage getGlyphPage(int codePoint);
	
	@Override
	public abstract void close();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<size=" + this.size + ">";
	}
	
}
