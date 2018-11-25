package io.msengine.client.renderer.texture;

import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public enum TextureWrapMode {
	
	REPEAT ( GL_REPEAT ),
	MIRRORED_REPEAT ( GL_MIRRORED_REPEAT ),
	CLAMP ( GL_CLAMP ),
	CLAMP_TO_BORDER ( GL_CLAMP_TO_BORDER ),
	CLAMP_TO_EDGE ( GL_CLAMP_TO_EDGE );
	
	public final int i;
	
	private TextureWrapMode(int i) {
		
		this.i = i;
		
	}
	
}
