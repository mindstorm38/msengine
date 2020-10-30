package io.msengine.client.graphics.texture.base;

import io.msengine.client.util.Symbol;

import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL44.GL_MIRROR_CLAMP_TO_EDGE;

public class SamplerParamWrap extends Symbol {
	
	public static final SamplerParamWrap REPEAT = from(GL_REPEAT);
	public static final SamplerParamWrap MIRRORED_REPEAT = from(GL_MIRRORED_REPEAT);
	public static final SamplerParamWrap CLAMP_TO_EDGE = from(GL_CLAMP_TO_EDGE);
	public static final SamplerParamWrap CLAMP_TO_BORDER = from(GL_CLAMP_TO_BORDER);
	public static final SamplerParamWrap MIRROR_CLAMP_TO_EDGE = from(GL_MIRROR_CLAMP_TO_EDGE);
	
	// Class //
	
	public SamplerParamWrap(int value) {
		super(value);
	}
	
	public static SamplerParamWrap from(int value) {
		return new SamplerParamWrap(value);
	}
	
}
