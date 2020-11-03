package io.msengine.client.graphics.texture.base;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL46;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture implements AutoCloseable, SamplerBase {
	
	// Common setups //
	
	public static final TextureSetup SETUP_LINEAR = new TextureSetup(tex -> {
		tex.setMinFilter(SamplerParamFilter.LINEAR);
		tex.setMagFilter(SamplerParamFilter.LINEAR);
	});
	
	public static final TextureSetup SETUP_NEAREST = new TextureSetup(tex -> {
		tex.setMinFilter(SamplerParamFilter.NEAREST);
		tex.setMagFilter(SamplerParamFilter.NEAREST);
	});
	
	public static final TextureSetup SETUP_LINEAR_KEEP = SETUP_LINEAR.withUnbind(false);
	public static final TextureSetup SETUP_NEAREST_KEEP = SETUP_NEAREST.withUnbind(false);
	
	// Utils for units and bindings //
	
	/**
	 * Get a texture unit ID.
	 * @param unit The index of the texture unit.
	 * @return The unit symbolic constant (GL constant {@link org.lwjgl.opengl.GL13#GL_TEXTURE0} for example).
	 */
	public static int getTextureUnit(int unit) {
		return GL_TEXTURE0 + unit;
	}
	
	/**
	 * Set the current texture unit used for next texture bindings.
	 * @param unit The unit (starting from 0).
	 */
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
	
	public static int getMaxTextureSize() {
		return glGetInteger(GL_MAX_TEXTURE_SIZE);
	}
	
	// Class //
	
	protected int name;
	protected final int target;
	protected final int targetVar;

	public Texture(int target, int targetVar, TextureSetup setup) {
		
		Objects.requireNonNull(setup, "Texture setup is null.");
		
		this.name = glGenTextures();
		this.target = target;
		this.targetVar = targetVar;
		
		setup.bind(this);
		setup.config(this);
		setup.unbind(this);
		
		if (!glIsTexture(this.name)) {
			throw new IllegalStateException("Failed to create texture object, maybe unset bind point.");
		}
		
	}
	
	public int getName() {
		return this.name;
	}
	
	public int getTarget() {
		return this.target;
	}
	
	public int getTargetVar() {
		return this.targetVar;
	}
	
	public boolean isValid() {
		return glIsTexture(this.name);
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The texture is no longer valid.");
		}
	}
	
	// Binding //
	
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
	
	public boolean isBound() {
		return this.name != 0 && glGetInteger(this.targetVar) == this.name;
	}
	
	public void checkBound() {
		if (!this.isBound()) {
			throw new IllegalStateException("This texture must be bound.");
		}
	}
	
	// Parameters //
	
	@Override
	public void setMinFilter(SamplerParamFilter minFilter) {
		this.checkBound();
		glTexParameterf(this.target, GL11.GL_TEXTURE_MIN_FILTER, minFilter.value);
	}
	
	@Override
	public void setMagFilter(SamplerParamFilter magFilter) {
		this.checkBound();
		glTexParameterf(this.target, GL11.GL_TEXTURE_MAG_FILTER, magFilter.value);
	}
	
	@Override
	public void setWidthWrap(SamplerParamWrap widthWrap) {
		this.checkBound();
		glTexParameterf(this.target, GL11.GL_TEXTURE_WRAP_S, widthWrap.value);
	}
	
	@Override
	public void setHeightWrap(SamplerParamWrap heightWrap) {
		this.checkBound();
		glTexParameterf(this.target, GL11.GL_TEXTURE_WRAP_T, heightWrap.value);
	}
	
	@Override
	public void setDepthWrap(SamplerParamWrap depthWrap) {
		this.checkBound();
		glTexParameterf(this.target, GL12.GL_TEXTURE_WRAP_R, depthWrap.value);
	}
	
	@Override
	public void setMaxAnisotropy(float maxAnisotropy) {
		this.checkBound();
		glTexParameterf(this.target, GL46.GL_TEXTURE_MAX_ANISOTROPY, maxAnisotropy);
	}
	
	// Level parameters //
	
	public int getLevelWidth(int level) {
		this.checkBound();
		return glGetTexLevelParameteri(this.target, level, GL11.GL_TEXTURE_WIDTH);
	}
	
	public int getLevelHeight(int level) {
		this.checkBound();
		return glGetTexLevelParameteri(this.target, level, GL11.GL_TEXTURE_HEIGHT);
	}
	
	public int getLevelDepth(int level) {
		this.checkBound();
		return glGetTexLevelParameteri(this.target, level, GL12.GL_TEXTURE_DEPTH);
	}
	
	public int getLevelInternalFormat(int level) {
		this.checkBound();
		return glGetTexLevelParameteri(this.target, level, GL11.GL_TEXTURE_INTERNAL_FORMAT);
	}
	
	// Close //
	
	@Override
	public void close() {
		if (glIsTexture(this.name)) {
			glDeleteTextures(this.name);
			this.name = 0;
		}
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<" + this.name + ">";
	}

}
