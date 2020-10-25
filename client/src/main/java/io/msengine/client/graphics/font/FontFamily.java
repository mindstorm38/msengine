package io.msengine.client.graphics.font;

import java.util.HashMap;
import java.util.Map;

public abstract class FontFamily implements AutoCloseable {
	
	private final Map<Float, Font> cachedSizes = new HashMap<>();
	
	/**
	 * @return The font family full name.
	 */
	public abstract String getName();
	
	/**
	 * Get the font of this family for a specified size, this use caching.
	 * @param size Font size in pixels.
	 * @return New font object.
	 */
	public Font getSize(float size) {
		Font cached = this.cachedSizes.get(size);
		if (cached == null) {
			cached = this.buildFontForSize(size);
			this.cachedSizes.put(size, cached);
		}
		return cached;
	}
	
	/**
	 * Method to implement that must build a font for specific size.
	 * @param size Font size in pixels.
	 * @return New font object.
	 */
	protected abstract Font buildFontForSize(float size);
	
	/**
	 * Close this font family, closing all sized font created with it.
	 */
	public void close() {
		this.cachedSizes.values().forEach(Font::close);
		this.cachedSizes.clear();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<name=" + this.getName() + ">";
	}
	
}
