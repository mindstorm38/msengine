package io.msengine.client.renderer.texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

/**
 * @deprecated Consider using newer texture API at {@link io.msengine.client.graphics.texture.base.SamplerParamFilter}
 */
@Deprecated
public enum TextureFilterMode {

	NEAREST ( GL_NEAREST ),
	LINEAR ( GL_LINEAR );
	
	public final int i;
	
	TextureFilterMode(int i) {
		this.i = i;
	}
	
}
