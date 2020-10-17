package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public abstract class IntBufferUniform extends BufferUniform<IntBuffer> {
	
	@Override
	protected IntBuffer provideBuffer() {
		return MemoryUtil.memAllocInt(this.getBufferSize());
	}
	
}
