package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public abstract class FloatBufferUniform extends BufferUniform<FloatBuffer> {
	
	protected abstract int size();
	
	@Override
	protected FloatBuffer provide() {
		return MemoryUtil.memAllocFloat(this.size());
	}
	
}
