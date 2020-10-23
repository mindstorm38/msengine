package io.msengine.client.graphics.texture;

import io.msengine.client.util.BufferAlloc;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture implements AutoCloseable, TextureProvider {
	
	public static final int CONFIG_UNIT = 31;
	
	/**
	 * Get a texture unit ID.
	 * @param unit The index of the texture unit.
	 * @return The unit symbolic constant (GL constant {@link org.lwjgl.opengl.GL13#GL_TEXTURE0} for example).
	 */
	public static int getTextureUnit(int unit) {
		return GL_TEXTURE0 + unit;
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
	
	public static void bindTexture(int unit, int target, int name) {
		glActiveTexture(getTextureUnit(unit));
		glBindTexture(target, name);
	}
	
	public static void unbindTexture(int unit, int target) {
		glActiveTexture(getTextureUnit(unit));
		glBindTexture(target, 0);
	}
	
	// Class //

	protected final int target;
	protected int name;

	public Texture(int target) {
		
		this.name = glGenTextures();
		this.target = target;
		
		this.completeTexture();
		
		if (!glIsTexture(this.name)) {
			throw new IllegalStateException("Failed to create texture object, maybe not set bound.");
		}
		
	}
	
	protected void completeTexture() { }
	
	public int getName() {
		return this.name;
	}
	
	public boolean isValid() {
		return glIsTexture(this.name);
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The texture is no longer valid.");
		}
	}
	
	public void bind() {
		bindTexture(this.target, this.name);
	}
	
	public void bind(int unit) {
		setTextureUnit(unit);
		bindTexture(this.target, this.name);
	}
	
	public void unbind() {
		unbindTexture(this.target);
	}
	
	public void unbind(int unit) {
		setTextureUnit(unit);
		unbindTexture(this.target);
	}
	
	public void setFilter(SamplerParamFilter minFilter, SamplerParamFilter magFilter) {
		this.bind(CONFIG_UNIT);
		glTexParameterf(this.target, GL_TEXTURE_MIN_FILTER, minFilter.value);
		glTexParameterf(this.target, GL_TEXTURE_MAG_FILTER, magFilter.value);
		this.unbind();
	}
	
	public void setWrap(SamplerParamWrap widthWrap) {
		this.bind(CONFIG_UNIT);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_S, widthWrap.value);
		this.unbind();
	}
	
	public void setWrap(SamplerParamWrap widthWrap, SamplerParamWrap heightWrap) {
		this.bind(CONFIG_UNIT);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_S, widthWrap.value);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_T, heightWrap.value);
		this.unbind();
	}
	
	public void setWrap(SamplerParamWrap widthWrap, SamplerParamWrap heightWrap, SamplerParamWrap depthWrap) {
		this.bind(CONFIG_UNIT);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_S, widthWrap.value);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_T, heightWrap.value);
		glTexParameterf(this.target, GL_TEXTURE_WRAP_R, depthWrap.value);
		this.unbind();
	}
	
	@Override
	public void close() {
		if (glIsTexture(this.name)) {
			glDeleteTextures(this.name);
			this.name = 0;
		}
	}
	
	@Override
	public int getTextureName() {
		return this.name;
	}

	public static void loadImageFromStream(InputStream stream, int initialSize, ImageLoadingConsumer consumer) throws IOException {
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
