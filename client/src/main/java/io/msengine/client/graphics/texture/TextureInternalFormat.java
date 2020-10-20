package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class TextureInternalFormat extends Symbol {
	
	public static final TextureInternalFormat RGBA2     = forStandard(GL_RGBA2);
	public static final TextureInternalFormat RGBA4     = forStandard(GL_RGBA4);
	
	public static final TextureInternalFormat RGBA8     = forStandard(GL_RGBA8);
	public static final TextureInternalFormat RGBA8_S   = forStandard(GL_RGBA8_SNORM);
	public static final TextureInternalFormat RGBA8_UI  = forIntegral(GL_RGBA8UI);
	public static final TextureInternalFormat RGBA8_I   = forIntegral(GL_RGBA8I);
	
	public static final TextureInternalFormat RGBA16    = forStandard(GL_RGBA16);
	public static final TextureInternalFormat RGBA16_S  = forStandard(GL_RGBA16_SNORM);
	public static final TextureInternalFormat RGBA16_UI = forIntegral(GL_RGBA16UI);
	public static final TextureInternalFormat RGBA16_I  = forIntegral(GL_RGBA16I);
	public static final TextureInternalFormat RGBA16_F  = forIntegral(GL_RGBA16F);
	
	public static final TextureInternalFormat RGBA32_UI  = forIntegral(GL_RGBA32UI);
	public static final TextureInternalFormat RGBA32_I   = forIntegral(GL_RGBA32I);
	public static final TextureInternalFormat RGBA32_F   = forIntegral(GL_RGBA32F);
	
	// Class //
	
	public final boolean integral;

	public TextureInternalFormat(int value, boolean integral) {
		super(value);
		this.integral = integral;
	}
	
	public static TextureInternalFormat forStandard(int value) {
		return new TextureInternalFormat(value, false);
	}
	
	public static TextureInternalFormat forIntegral(int value) {
		return new TextureInternalFormat(value, true);
	}

}
