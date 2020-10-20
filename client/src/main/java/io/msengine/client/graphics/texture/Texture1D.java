package io.msengine.client.graphics.texture;

import static org.lwjgl.opengl.GL11.*;

public class Texture1D extends Texture {
	
	@Override
	protected int getTarget() {
		return GL_TEXTURE_1D;
	}
	
	public void upload(int level, int internalFormat, int width, int height) {
		this.bind();
		glTexImage1D(GL_TEXTURE_1D, 0, internalFormat, width, height);
	}
	
}
