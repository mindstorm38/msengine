package io.msengine.client.graphics.texture;

import io.msengine.client.util.BufferAlloc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture implements AutoCloseable {
	
	/**
	 * Get a texture unit ID.
	 * @param unit The index of the texture unit.
	 * @return The unit symbolic constant (GL constant {@link org.lwjgl.opengl.GL13#GL_TEXTURE0} for example).
	 */
	public static int getTextureUnit(int unit) {
		return GL_TEXTURE0 + unit;
	}
	
	// Class //

	protected final int target;
	protected int name;

	public Texture(int target) {
		this.name = glGenTextures();
		if (!glIsTexture(this.name)) {
			throw new IllegalStateException("Failed to create texture object.");
		}
		this.target = target;
	}
	
	public boolean isValid() {
		return glIsTexture(this.name);
	}
	
	public int getName() {
		return this.name;
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The texture is not yet valid.");
		}
	}

	public static void setTextureUnit(int unit) {
		glActiveTexture(getTextureUnit(unit));
	}

	public static void bindTexture(int target, int name) {
		glBindTexture(target, name);
	}

	public static void unbindTexture(int target) {
		glBindTexture(target, 0);
	}
	
	public void bind() {
		this.checkValid();
		bindTexture(this.target, this.name);
	}
	
	public void bind(int unit) {
		this.checkValid();
		setTextureUnit(unit);
		bindTexture(this.target, this.name);
	}
	
	@Override
	public void close() {
		if (glIsTexture(this.name)) {
			glDeleteTextures(this.name);
			this.name = 0;
		}
	}

	public static void loadImageFromStream(InputStream stream, int initialSize, DynTexture.ImageLoadingConsumer consumer) throws IOException {
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
		MemoryUtil.memFree(buf);
	}

	@FunctionalInterface
	public interface ImageLoadingConsumer {
		void accept(ByteBuffer buf, int width, int height);
	}

}
