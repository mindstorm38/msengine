package io.msengine.client.graphics.texture;

import static org.lwjgl.opengl.GL11.*;

public class Texture1D extends Texture {
	
	@Override
	protected int getTarget() {
		return GL_TEXTURE_1D;
	}
	
	public void allocate(int level, TextureInternalFormat internalFormat, int width) {
		this.bind();
	}
	
	public void upload(int level, TextureInternalFormat internalFormat, int width, TextureClientFormat format, TextureClientType type) {
		this.bind();
		// glTexImage1D(GL_TEXTURE_1D, 0, format.value, width, 0, format.value, type.value);
	}
	
}
