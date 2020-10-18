package io.msengine.client.graphics.util;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

public class BufferAlloc {

	// Off-heap Stack allocation //
	
	public static <B extends Buffer> void allocStack(int count, MemoryStackAllocator<B> alloc, Consumer<B> cb) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			cb.accept(alloc.alloc(stack, count));
		}
	}
	
	public static void allocStackByte(int count, Consumer<ByteBuffer> cb) {
		allocStack(count, MemoryStack::malloc, cb);
	}
	
	public static void allocStackInt(int count, Consumer<IntBuffer> cb) {
		allocStack(count, MemoryStack::mallocInt, cb);
	}
	
	public static void allocStackFloat(int count, Consumer<FloatBuffer> cb) {
		allocStack(count, MemoryStack::mallocFloat, cb);
	}
	
	@FunctionalInterface
	public interface MemoryStackAllocator<B extends Buffer> {
		B alloc(MemoryStack stack, int count);
	}
	
	// Off-heap alloc //
	
	public static <B extends Buffer> void alloc(int count, MemoryAllocator<B> alloc, Consumer<B> cb) {
		B buf = null;
		try {
			cb.accept(buf = alloc.alloc(count));
		} finally {
			if (buf != null) {
				MemoryUtil.memFree(buf);
			}
		}
	}
	
	public static void allocByte(int count, Consumer<ByteBuffer> cb) {
		alloc(count, MemoryUtil::memAlloc, cb);
	}
	
	public static void allocInt(int count, Consumer<IntBuffer> cb) {
		alloc(count, MemoryUtil::memAllocInt, cb);
	}
	
	public static void allocFloat(int count, Consumer<FloatBuffer> cb) {
		alloc(count, MemoryUtil::memAllocFloat, cb);
	}
	
	@FunctionalInterface
	public interface MemoryAllocator<B extends Buffer> {
		B alloc(int count);
	}

}
