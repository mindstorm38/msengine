package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL30.*;

public class TextureClientFormat extends Symbol {

	public static final TextureClientFormat RED = forStandard(GL_RED);
	public static final TextureClientFormat GREEN = forStandard(GL_GREEN);
	public static final TextureClientFormat BLUE = forStandard(GL_BLUE);
	public static final TextureClientFormat RG = forStandard(GL_RG);
	public static final TextureClientFormat RGB = forStandard(GL_RGB);
	public static final TextureClientFormat BGR = forStandard(GL_BGR);
	public static final TextureClientFormat RGBA = forStandard(GL_RGBA);
	public static final TextureClientFormat BGRA = forStandard(GL_BGRA);

	public static final TextureClientFormat RED_INTEGRAL = forIntegral(GL_RED_INTEGER);
	public static final TextureClientFormat GREEN_INTEGRAL = forIntegral(GL_GREEN_INTEGER);
	public static final TextureClientFormat BLUE_INTEGRAL = forIntegral(GL_BLUE_INTEGER);
	public static final TextureClientFormat RG_INTEGRAL = forIntegral(GL_RG_INTEGER);
	public static final TextureClientFormat RGB_INTEGRAL = forIntegral(GL_RGB_INTEGER);
	public static final TextureClientFormat BGR_INTEGRAL = forIntegral(GL_BGR_INTEGER);
	public static final TextureClientFormat RGBA_INTEGRAL = forIntegral(GL_RGBA_INTEGER);
	public static final TextureClientFormat BGRA_INTEGRAL = forIntegral(GL_BGRA_INTEGER);
	
	public static final TextureClientFormat DEPTH = forStandard(GL_DEPTH_COMPONENT);
	public static final TextureClientFormat STENCIL = forStandard(GL_STENCIL_INDEX);
	public static final TextureClientFormat DEPTH_STENCIL = forStandard(GL_DEPTH_STENCIL);

	// Class //

	public final boolean integral;
	
	public TextureClientFormat(int value, boolean integral) {
		super(value);
		this.integral = integral;
	}
	
	public static TextureClientFormat forStandard(int value) {
		return new TextureClientFormat(value, false);
	}
	
	public static TextureClientFormat forIntegral(int value) {
		return new TextureClientFormat(value, true);
	}

}
