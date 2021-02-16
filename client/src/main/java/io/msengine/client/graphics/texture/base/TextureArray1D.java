package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL30;

public class TextureArray1D extends Texture {
	
	public TextureArray1D(TextureSetup setup) {
		super(GL30.GL_TEXTURE_1D_ARRAY, GL30.GL_TEXTURE_BINDING_1D_ARRAY, setup);
	}
	
	public TextureArray1D() {
		this(TextureSetup.DEFAULT);
	}
	
}
