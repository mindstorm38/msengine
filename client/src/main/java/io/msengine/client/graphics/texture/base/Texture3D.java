package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL12;

public class Texture3D extends Texture {
	
	public Texture3D(TextureSetup setup) {
		super(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BINDING_3D, setup);
	}
	
	public Texture3D() {
		this(SETUP_LINEAR);
	}
	
}
