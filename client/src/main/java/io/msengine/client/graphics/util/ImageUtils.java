package io.msengine.client.graphics.util;

import io.msengine.client.util.BufferAlloc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class ImageUtils {
	
	public static void loadImageFromStream(InputStream stream, int initialSize, boolean free, ImageLoadingConsumer consumer) throws IOException {
		ByteBuffer buf = BufferAlloc.fromInputStream(stream, initialSize);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.callocInt(1);
			IntBuffer height = stack.callocInt(1);
			IntBuffer channels = stack.callocInt(1);
			ByteBuffer buffer = stbi_load_from_memory(buf, width, height, channels, 4);
			if (buffer == null) {
				throw new IOException("Failed to load image from memory (using STB): " + stbi_failure_reason());
			}
			consumer.accept(buffer, width.get(), height.get());
		}
		if (free) MemoryUtil.memFree(buf);
	}
	
	public static void loadImageFromStream(InputStream stream, int initialSize, ImageLoadingConsumer consumer) throws IOException {
		loadImageFromStream(stream, initialSize, true, consumer);
	}
		
		@FunctionalInterface
	public interface ImageLoadingConsumer {
		void accept(ByteBuffer buf, int width, int height);
	}
	
}
