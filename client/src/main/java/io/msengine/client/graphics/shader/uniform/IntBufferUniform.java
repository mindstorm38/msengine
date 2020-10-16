package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public abstract class IntBufferUniform extends BufferUniform<IntBuffer> {
	
	protected abstract int size();
	
	@Override
	protected IntBuffer provide() {
		return MemoryUtil.memAllocInt(this.size());
	}
	
}
