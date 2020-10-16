package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;

public abstract class BufferUniform<B extends Buffer> extends Uniform {
	
	protected B buffer;
	
	@Override
	public void setup(String identifier, int location) {
		super.setup(identifier, location);
		this.buffer = this.provide();
	}
	
	protected abstract B provide();
	
	@Override
	public void close() {
		MemoryUtil.memFree(this.buffer);
	}
	
}
