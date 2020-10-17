package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public abstract class FloatBufferUniform extends BufferUniform<FloatBuffer> {
	
	@Override
	protected FloatBuffer provideBuffer() {
		return MemoryUtil.memAllocFloat(this.getBufferSize());
	}
	
}
