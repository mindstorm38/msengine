package io.msengine.client.graphics.font;

public abstract class Font implements AutoCloseable {

	public abstract GlyphPage getGlyphPage(int codepoint);
	
	@Override
	public abstract void close();
	
}
