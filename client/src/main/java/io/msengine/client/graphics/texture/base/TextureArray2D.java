package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL30;

public class TextureArray2D extends Texture {
	
	public TextureArray2D(TextureSetup setup) {
		super(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BINDING_2D_ARRAY, setup);
	}
	
	public TextureArray2D() {
		this(TextureSetup.DEFAULT);
	}
	
}
