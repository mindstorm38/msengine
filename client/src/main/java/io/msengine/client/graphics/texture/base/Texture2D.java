package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL11;

public class Texture2D extends Texture {
	
	public Texture2D(TextureSetup setup) {
		super(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BINDING_2D, setup);
	}
	
	public Texture2D() {
		this(SETUP_LINEAR);
	}
	
}
