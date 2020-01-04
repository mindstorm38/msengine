package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL13.GL_MAX_TEXTURE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_COORDS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL30.GL_MAX_COLOR_ATTACHMENTS;
import static org.lwjgl.opengl.GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS;
import static io.msengine.common.util.GameLogger.*;

import java.awt.Color;
import java.awt.Graphics2D;

import io.msengine.client.renderer.texture.DynamicTexture;
import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;

/**
 * 
 * User to store constants OpenGL object or informations about the current context
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class RenderConstantFields {
	
	// Singleton \\
	
	private static RenderConstantFields INSTANCE = null;
	
	public static RenderConstantFields getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( RenderConstantFields.class );
		return INSTANCE;
	}
	
	// Class \\
	
	/**
	 * Maximum OpenGL texture size
	 */
	private int maxTextureSize = -1;
	
	/**
	 * Maximum OpenGL attribute count per shader program
	 */
	private int maxAttribCount = -1;
	
	private float maxTextureCoords = -1f;
	
	private int maxTextureUnits = -1;
	
	private int maxColorAttachment = -1;
	
	private int maxUniformBufferBindings = -1;
	
	/**
	 * Missing texture object
	 */
	private DynamicTexture missingTexture = null;
	
	/**
	 * No texture object (white 1x1 texture)
	 */
	private DynamicTexture noTexture = null;
	
	public RenderConstantFields() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( RenderConstantFields.class );
		INSTANCE = this;
		
	}
	
	public void init() {
		
		this.maxTextureSize = glGetInteger( GL_MAX_TEXTURE_SIZE );
		this.maxAttribCount = glGetInteger( GL_MAX_VERTEX_ATTRIBS );
		this.maxTextureCoords = glGetFloat( GL_MAX_TEXTURE_COORDS );
		this.maxTextureUnits = glGetInteger( GL_MAX_TEXTURE_UNITS );
		this.maxColorAttachment = glGetInteger( GL_MAX_COLOR_ATTACHMENTS );
		this.maxUniformBufferBindings = glGetInteger( GL_MAX_UNIFORM_BUFFER_BINDINGS );
		
		// Shared graphics var
		Graphics2D graphics;
		
		// - Missing texture
		this.missingTexture = new DynamicTexture( 2, 2 );
		graphics = this.missingTexture.getImage().createGraphics();
		graphics.setColor( new Color( 0, 0, 0 ) );
		graphics.fillRect( 0, 0, 2, 2 );
		graphics.setColor( new Color( 255, 0, 255 ) );
		graphics.fillRect( 0, 0, 1, 1 );
		graphics.fillRect( 1, 1, 1, 1 );
		this.missingTexture.updateTexture();
		
		// - No texture
		this.noTexture = new DynamicTexture( 1, 1 );
		graphics = this.noTexture.getImage().createGraphics();
		graphics.setColor( new Color( 255, 255, 255 ) );
		graphics.fillRect( 0, 0, 1, 1 );
		this.noTexture.updateTexture();
		
		// - Log values
		LOGGER.info( "Constants fields :" );
		LOGGER.info( "- Maximum texture size : " + this.maxTextureSize );
		LOGGER.info( "- Maximum attribute count : " + this.maxAttribCount );
		LOGGER.info( "- Maximum texture coords : " + this.maxTextureCoords );
		LOGGER.info( "- Maximum texture units : " + this.maxTextureUnits );
		LOGGER.info( "- Maximum color attachmend : " + this.maxColorAttachment );
		LOGGER.info( "- Maximum uniform buffer bindings : " + this.maxUniformBufferBindings );
		
	}
	
	public void stop() {
		
		// - Missing texture
		this.missingTexture.delete();
		
	}
	
	public int getMaxTextureSize() {
		return this.maxTextureSize;
	}
	
	public int getMaxAttribCount() {
		return this.maxAttribCount;
	}
	
	public float getMaxTextureCoords() {
		return this.maxTextureCoords;
	}
	
	public int getMaxTextureUnits() {
		return this.maxTextureUnits;
	}
	
	public int getMaxColorAttachment() {
		return this.maxColorAttachment;
	}
	
	public int getMaxUniformBufferBindings() {
		return this.maxUniformBufferBindings;
	}
	
	public DynamicTexture getMissingTexture() {
		return this.missingTexture;
	}
	
	public DynamicTexture getNoTexture() {
		return this.noTexture;
	}
	
}
