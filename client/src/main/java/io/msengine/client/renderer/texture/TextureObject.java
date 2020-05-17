package io.msengine.client.renderer.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.util.Utils;

/**
 *
 * TextureObject is the base wrapper class for native OpenGL textures.
 *
 * @author Theo Rozier
 *
 */
public class TextureObject implements ShaderSamplerObject {
	
	// Static \\
	
	/**
	 * Get a texture unit ID.
	 * @param idx The index of the texture unit (from 0 to 31 included).
	 * @return The sampler unit ID (GL constant {@link org.lwjgl.opengl.GL13#GL_TEXTURE0} for example).
	 */
	public static int getTextureActiveConstant(int idx) {
		if (idx < 0 || idx >= 32) {
			throw new IllegalArgumentException("Invalid active texture index " + idx + ". Must be between 0 and 31.");
		} else {
			return GL_TEXTURE0 + idx;
		}
	}
	
	/**
	 * Bind a GL texture id to the current sampler unit.
	 * @param id The GL texture id.
	 */
	public static void bindTexture(int id) {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Bind a GL texture id (like {@link #bindTexture(int)}),
	 * but before set active texture ({@link org.lwjgl.opengl.GL13#glActiveTexture(int)}) to the texture sampler unit idx (see {@link #getTextureActiveConstant(int)}).
	 * @param id The GL texture ID.
	 * @param activeTexture The index of the texture unit.
	 */
	public static void bindTexture(int id, int activeTexture) {
		glActiveTexture(getTextureActiveConstant(activeTexture));
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Unbind current texture from current texture unit.
	 */
	public static void unbind() {
		bindTexture(0);
	}
	
	/**
	 * Unbind the current texture in the specified texture unit.
	 * @param activeTexture Texture unit index.
	 */
	public static void unbind(int activeTexture) {
		bindTexture(0, activeTexture);
	}
	
	// Class \\
	
	private final int width;
	private final int height;
	private final int id;
	private boolean deleted;
	
	public TextureObject(int width, int height) {
		
		this.width = width;
		this.height = height;
		this.deleted = false;
		
		// Generating texture
		this.id = glGenTextures();
		
		// Binding generated texture
		this.bind();
		
		// Allocating texture
		glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null );
		
		// Applying default texture paramaters
		this.setWrap( TextureWrapMode.CLAMP_TO_BORDER, TextureWrapMode.CLAMP_TO_BORDER );
		this.setFilter( TextureFilterMode.NEAREST, TextureFilterMode.NEAREST );
		
	}
	
	public TextureObject(BufferedImage image) {
		
		this( image.getWidth(), image.getHeight() );
		
		// Uploading texture image
		this.upload( image );
		
	}
	
	public void bind() {
		this.checkDeleted();
		bindTexture( this.id );
	}
	
	public void bind(int active) {
		this.checkDeleted();
		bindTexture( this.id, active );
		this.bind();
	}
	
	public void setWrap(TextureWrapMode hWrap, TextureWrapMode vWrap) {
		this.bind();
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, hWrap.i );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, vWrap.i );
		unbind();
	}
	
	public void setFilter(TextureFilterMode minFilter, TextureFilterMode magFilter) {
		this.bind();
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter.i );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter.i );
		unbind();
	}
	
	public void upload(BufferedImage image, int x, int y, int width, int height) {
		
		if ((x + width) > this.width || (y + height) > this.height)
			throw new IndexOutOfBoundsException( "Max size of texture reached" );
		
		this.bind();
		
		// Uploading sub image from read pixels
		glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, Utils.getImageBuffer(image, x, y, width, height));
		
		// Unbinding current texture
		unbind();
		
	}
	
	public void upload(ByteBuffer rawBuffer, int x, int y, int width, int height) {
		
		if ((x + width) > this.width || (y + height) > this.height)
			throw new IndexOutOfBoundsException( "Max size of texture reached" );
		
		this.bind();
		glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, rawBuffer);
		unbind();
		
	}
	
	public void upload(BufferedImage image) {
		this.upload( image, 0, 0, image.getWidth(), image.getHeight() );
	}
	
	public void delete() {
		this.checkDeleted();
		glDeleteTextures( this.id );
		this.deleted = true;
	}
	
	public boolean isDeleted() {
		return this.deleted;
	}
	
	private void checkDeleted() {
		if ( this.deleted ) throw new IllegalStateException("Unusable TextureObject because it has been deleted.");
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getId() {
		return this.id;
	}
	
	@Override
	public int getSamplerId() {
		return this.id;
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		if ( !this.deleted ) {
			
			this.delete();
			
		}
		
	}

}
