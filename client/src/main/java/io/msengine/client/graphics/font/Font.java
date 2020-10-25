package io.msengine.client.graphics.font;

public abstract class Font implements AutoCloseable {

	public abstract GlyphPage getGlyphPage(int codePoint);
	
	public Glyph getGlyph(int codePoint) {
		GlyphPage page = this.getGlyphPage(codePoint);
		return page == null ? null : page.getGlyph(codePoint);
	}
	
	@Override
	public abstract void close();
	
}
