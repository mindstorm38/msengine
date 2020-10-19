package io.msengine.client.graphics.texture;

import static org.lwjgl.opengl.GL11.*;

public class Texture1D extends Texture {
	
	@Override
	protected int getTarget() {
		return GL_TEXTURE_1D;
	}
	
	public void upload() {
		this.bind();
	}
	
}
