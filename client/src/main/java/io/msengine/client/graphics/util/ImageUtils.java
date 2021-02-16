package io.msengine.client.graphics.util;

import io.msengine.client.util.BufferAlloc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class ImageUtils {
	
	/**
	 * Free an image returned by a previous call to {@link #loadImageFromStream(InputStream, int, boolean, ImageLoadingConsumer)}.
	 * @param imgBuf The image buffer.
	 */
	public static void freeImage(ByteBuffer imgBuf) {
		stbi_image_free(imgBuf);
	}
	
	/**
	 * <p>Load an image from an {@link InputStream}, and return the image
	 * buffer (with 4 bytes component r/g/b/a) with its size.</p>
	 * <p><b>If <code>free == false</code>, then you have to free the returned buffer by calling {@link #freeImage(ByteBuffer)}.</b></p>
	 * @param stream The stream to read from.
	 * @param initialSize The initial buffer capacity used to read the stream.
	 * @param free Whether to free the image buffer after called the consumer.
	 * @param consumer The consumer called with image buffer, width and height.
	 * @throws IOException If the method fails to read the input stream, or if the image failed to read.
	 */
	public static void loadImageFromStream(InputStream stream, int initialSize, boolean free, ImageLoadingConsumer consumer) throws IOException {
		ByteBuffer buf = BufferAlloc.fromInputStream(stream, initialSize);
		ByteBuffer img = null;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.callocInt(1);
			IntBuffer height = stack.callocInt(1);
			IntBuffer channels = stack.callocInt(1);
			img = stbi_load_from_memory(buf, width, height, channels, 4);
			if (img == null) {
				throw new IOException("Failed to load image from memory (using STB): " + stbi_failure_reason());
			}
			consumer.accept(img, width.get(), height.get());
		} finally {
			MemoryUtil.memFree(buf);
			if (img != null && free) {
				stbi_image_free(img);
			}
		}
	}
	
	public static void loadImageFromStream(InputStream stream, int initialSize, ImageLoadingConsumer consumer) throws IOException {
		loadImageFromStream(stream, initialSize, true, consumer);
	}
	
	@FunctionalInterface
	public interface ImageLoadingConsumer {
		void accept(ByteBuffer buf, int width, int height);
	}
	
}
