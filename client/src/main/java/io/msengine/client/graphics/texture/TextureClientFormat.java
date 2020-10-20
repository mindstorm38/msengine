package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL30.*;

public class TextureClientFormat extends Symbol {

	public static final TextureClientFormat RED = new TextureClientFormat(GL_RED);
	public static final TextureClientFormat GREEN = new TextureClientFormat(GL_GREEN);
	public static final TextureClientFormat BLUE = new TextureClientFormat(GL_BLUE);
	public static final TextureClientFormat RG = new TextureClientFormat(GL_RG);
	public static final TextureClientFormat RGB = new TextureClientFormat(GL_RGB);
	public static final TextureClientFormat BGR = new TextureClientFormat(GL_BGR);
	public static final TextureClientFormat RGBA = new TextureClientFormat(GL_RGBA);
	public static final TextureClientFormat BGRA = new TextureClientFormat(GL_BGRA);
	public static final TextureClientFormat DEPTH = new TextureClientFormat(GL_DEPTH_COMPONENT);
	public static final TextureClientFormat STENCIL = new TextureClientFormat(GL_STENCIL_INDEX);
	public static final TextureClientFormat DEPTH_STENCIL = new TextureClientFormat(GL_DEPTH_STENCIL);

	// Class //

	public TextureClientFormat(int value) {
		super(value);
	}

}
