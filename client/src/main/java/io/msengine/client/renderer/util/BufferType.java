package io.msengine.client.renderer.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

public class BufferType<E extends Buffer> {
	
	public static final BufferType<IntBuffer> INT = new BufferType<>(MemoryUtil::memAllocInt);
	public static final BufferType<FloatBuffer> FLOAT = new BufferType<>(MemoryUtil::memAllocFloat);
	
	private Function<Integer, E> allocator;
	
	private BufferType(Function<Integer, E> allocator) {
		this.allocator = allocator;
	}
	
	public void alloc(int size, Consumer<E> consumer) {
		
		E buf = null;
		
		try {
			
			buf = this.allocator.apply(size);
			consumer.accept(buf);
			
		} finally {
			BufferUtils.safeFree(buf);
		}
		
	}
	
}
