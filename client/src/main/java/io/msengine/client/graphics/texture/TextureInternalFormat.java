package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class TextureInternalFormat extends Symbol {

	public static final TextureInternalFormat R = new TextureInternalFormat(GL_RED);
	public static final TextureInternalFormat RG = new TextureInternalFormat(GL_RG);
	public static final TextureInternalFormat RGB = new TextureInternalFormat(GL_RGB);
	public static final TextureInternalFormat RGBA = new TextureInternalFormat(GL_RGBA);

	public static final TextureInternalFormat R8 = new TextureInternalFormat(GL_R8);

	// Class //

	public TextureInternalFormat(int value) {
		super(value);
	}

}
