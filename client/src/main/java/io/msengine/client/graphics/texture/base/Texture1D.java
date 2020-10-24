package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL11;

public class Texture1D extends Texture {
	
	public Texture1D(TextureSetup setup) {
		super(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_BINDING_1D, setup);
	}
	
	public Texture1D() {
		this(SETUP_LINEAR);
	}
	
}
