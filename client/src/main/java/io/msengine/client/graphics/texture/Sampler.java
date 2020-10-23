package io.msengine.client.graphics.texture;

import static org.lwjgl.opengl.GL33.*;

public class Sampler implements AutoCloseable {
	
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
	
	@Override
	public void close() throws Exception {
		if (glIsSampler(this.name)) {
			glDeleteSamplers(this.name);
			this.name = 0;
		}
	}
	
}
