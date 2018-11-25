package io.msengine.client.renderer.texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public enum TextureFilterMode {

	NEAREST ( GL_NEAREST ),
	LINEAR ( GL_LINEAR );
	
	public final int i;
	
	private TextureFilterMode(int i) {
		
		this.i = i;
		
	}
	
}
