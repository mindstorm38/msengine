package io.msengine.client.util;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.function.Consumer;

public final class BufferAlloc {

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

	// Utils //

	public static ByteBuffer fromInputStream(InputStream stream, int initialSize) throws IOException {
		
		Objects.requireNonNull(stream, "Input stream is null.");
		ByteBuffer buffer = MemoryUtil.memAlloc(initialSize);

		try (ReadableByteChannel channel = Channels.newChannel(stream)) {
			int read;
			while ((read = channel.read(buffer)) != -1) {
				if (buffer.remaining() == 0) {
					int pos = buffer.position();
					ByteBuffer newBuffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + 2048);
					if (newBuffer == null) {
						MemoryUtil.memFree(buffer);
						throw new IOException("Failed to realloc buffer when reading stream.");
					} else {
						buffer = newBuffer;
					}
					buffer.position(pos);
				}
			}

		}

		buffer.flip();
		return buffer;

	}

	public static ByteBuffer fromInputStream(InputStream stream) throws IOException {
		return fromInputStream(stream, 4096);
	}

}
