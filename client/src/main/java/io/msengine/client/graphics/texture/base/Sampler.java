package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL46;

import static org.lwjgl.opengl.GL33.*;

public class Sampler implements AutoCloseable, SamplerBase {
	
	private int name;
	
	public Sampler() {
		this.name = glGenSamplers();
		if (!glIsSampler(this.name)) {
			throw new IllegalStateException("Failed to create sampler object.");
		}
	}
	
	public boolean isValid() {
		return glIsSampler(this.name);
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The sampler is no longer valid.");
		}
	}
	
	// Binding //
	
	public void bind(int unit) {
		this.checkValid();
		glBindSampler(unit, this.name);
	}
	
	public static void unbind(int unit) {
		glBindSampler(unit, 0);
	}
	
	// Parameters //
	
	@Override
	public void setMinFilter(SamplerParamFilter minFilter) {
		glSamplerParameteri(this.name, GL11.GL_TEXTURE_MIN_FILTER, minFilter.value);
	}
	
	@Override
	public void setMagFilter(SamplerParamFilter magFilter) {
		glSamplerParameteri(this.name, GL11.GL_TEXTURE_MAG_FILTER, magFilter.value);
	}
	
	@Override
	public void setWidthWrap(SamplerParamWrap widthWrap) {
		glSamplerParameteri(this.name, GL11.GL_TEXTURE_WRAP_S, widthWrap.value);
	}
	
	@Override
	public void setHeightWrap(SamplerParamWrap heightWrap) {
		glSamplerParameteri(this.name, GL11.GL_TEXTURE_WRAP_T, heightWrap.value);
	}
	
	@Override
	public void setDepthWrap(SamplerParamWrap depthWrap) {
		glSamplerParameteri(this.name, GL12.GL_TEXTURE_WRAP_R, depthWrap.value);
	}
	
	@Override
	public void setMaxAnisotropy(float maxAnisotropy) {
		glSamplerParameterf(this.name, GL46.GL_TEXTURE_MAX_ANISOTROPY, maxAnisotropy);
	}
	
	// Close //
	
	@Override
	public void close() {
		if (glIsSampler(this.name)) {
			glDeleteSamplers(this.name);
			this.name = 0;
		}
	}
	
}
