package io.msengine.client.graphics.shader.uniform;

import io.msengine.client.graphics.shader.ShaderProgram;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;

public abstract class BufferUniform<B extends Buffer> extends Uniform {
	
	protected B buffer;
	
	@Override
	public void setup(ShaderProgram program, String identifier, int location) {
		super.setup(program, identifier, location);
		this.buffer = this.provideBuffer();
	}
	
	protected abstract int getBufferSize();
	protected abstract B provideBuffer();
	
	protected B ensureBuffer() {
		if (this.buffer == null) {
			throw new IllegalStateException("The uniform buffer is not available or previously closed.");
		} else {
			return this.buffer;
		}
	}
	
	@Override
	public void close() {
		if (this.buffer != null) {
			MemoryUtil.memFree(this.buffer);
			this.buffer = null;
		}
	}
	
}
