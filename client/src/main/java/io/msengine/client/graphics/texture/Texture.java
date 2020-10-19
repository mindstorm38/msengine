package io.msengine.client.graphics.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public abstract class Texture implements AutoCloseable {
	
	/**
	 * Get a texture unit ID.
	 * @param idx The index of the texture unit.
	 * @return The sampler unit ID (GL constant {@link org.lwjgl.opengl.GL13#GL_TEXTURE0} for example).
	 */
	public static int getTextureActive(int idx) {
		return GL_TEXTURE0 + idx;
	}
	
	// Class //

	private int name;
	
	public Texture() {
		this.name = glGenTextures();
		if (!glIsTexture(this.name)) {
			throw new IllegalStateException("Failed to create texture object.");
		}
	}
	
	protected abstract int getTarget();
	
	public boolean isValid() {
		return glIsTexture(this.name);
	}
	
	public int getName() {
		return this.name;
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The texture is not yet valid.");
		}
	}
	
	public void bind() {
		this.checkValid();
		glBindTexture(this.getTarget(), this.name);
	}
	
	public void bind(int active) {
		this.checkValid();
		glActiveTexture(getTextureActive(active));
		glBindTexture(this.getTarget(), this.name);
	}
	
	@Override
	public void close() {
		if (glIsTexture(this.name)) {
			glDeleteTextures(this.name);
			this.name = 0;
		}
	}
	
}
