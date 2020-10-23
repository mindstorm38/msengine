package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL11.*;

public class SamplerParamFilter extends Symbol {
	
	public static final SamplerParamFilter NEAREST = from(GL_NEAREST);
	public static final SamplerParamFilter LINEAR = from(GL_LINEAR);
	public static final SamplerParamFilter NEAREST_MIPMAP_NEAREST = from(GL_NEAREST_MIPMAP_NEAREST);
	public static final SamplerParamFilter LINEAR_MIPMAP_NEAREST = from(GL_LINEAR_MIPMAP_NEAREST);
	public static final SamplerParamFilter NEAREST_MIPMAP_LINEAR = from(GL_NEAREST_MIPMAP_LINEAR);
	public static final SamplerParamFilter LINEAR_MIPMAP_LINEAR = from(GL_LINEAR_MIPMAP_LINEAR);
	
	// Class //
	
	public SamplerParamFilter(int value) {
		super(value);
	}
	
	public static SamplerParamFilter from(int value) {
		return new SamplerParamFilter(value);
	}
	
}
