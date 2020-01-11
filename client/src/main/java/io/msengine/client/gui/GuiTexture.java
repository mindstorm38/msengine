package io.msengine.client.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.client.renderer.texture.TextureObject;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

import static io.msengine.client.renderer.vertex.type.GuiFormat.*;

public class GuiTexture extends GuiObject {

	protected IndicesDrawBuffer buffer;
	protected boolean updateVertices;
	protected boolean updateTexCoords;
	
	protected TextureObject texture;
	protected float textureX;
	protected float textureY;
	protected float textureWidth;
	protected float textureHeight;
	
	@Override
	protected void init() {

		this.buffer = this.renderer.createDrawBuffer( false, true );
		
		this.initBuffers();
		
	}

	@Override
	protected void stop() {

		this.buffer.delete();
		this.buffer = null;
		
	}
	
	private void initBuffers() {
		
		IntBuffer indicesBuffer = null;
		
		try {
			
			indicesBuffer = MemoryUtil.memAllocInt( this.buffer.setIndicesCount( 6 ) );
			
			indicesBuffer.put( 0 ).put( 1 ).put( 3 );
			indicesBuffer.put( 1 ).put( 2 ).put( 3 );
			
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.allocateVboData( GUI_POSITION, 8 << 2, BufferUsage.DYNAMIC_DRAW );
			this.buffer.allocateVboData( GUI_TEX_COORD, 8 << 2, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadIboData( indicesBuffer, BufferUsage.STATIC_DRAW );
			
		} finally {
			
			if ( indicesBuffer != null ) MemoryUtil.memFree( indicesBuffer );
			
		}
		
		this.updateVerticesBuffer();
		this.updateTexCoordsBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		FloatBuffer verticesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat( 8 );
			
			verticesBuffer.put( 0 ).put( 0 );
			verticesBuffer.put( 0 ).put( this.height );
			verticesBuffer.put( this.width ).put( this.height );
			verticesBuffer.put( this.width ).put( 0 );
			
			verticesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboSubData( GUI_POSITION, 0, verticesBuffer );
			
		} finally {
			
			if ( verticesBuffer != null ) MemoryUtil.memFree( verticesBuffer );
			
		}
		
		this.updateVertices = false;
		
	}
	
	private void updateTexCoordsBuffer() {
		
		FloatBuffer texCoordsBuffer = null;
		
		try {
			
			texCoordsBuffer = MemoryUtil.memAllocFloat( 8 );
			
			texCoordsBuffer.put( this.textureX ).put( this.textureY );
			texCoordsBuffer.put( this.textureX ).put( this.textureY + this.textureHeight );
			texCoordsBuffer.put( this.textureX + this.textureWidth ).put( this.textureY + this.textureHeight );
			texCoordsBuffer.put( this.textureX + this.textureWidth ).put( this.textureY );
			
			texCoordsBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboSubData( GUI_TEX_COORD, 0, texCoordsBuffer );
			
		} finally {
			
			if ( texCoordsBuffer != null ) MemoryUtil.memFree( texCoordsBuffer );
			
		}
		
		this.updateTexCoords = false;
		
	}

	@Override
	public void render(float alpha) {
		
		if ( this.texture == null )
			return;
		
		if ( this.updateTexCoords )
			this.updateTexCoordsBuffer();
		
		if ( this.updateVertices )
			this.updateVerticesBuffer();
		
		this.model.push().translate( this.xOffset, this.yOffset ).apply();
			
			this.renderer.setTextureSampler( this.texture );
			
				this.buffer.drawElements();
			
			this.renderer.resetTextureSampler();
			
		this.model.pop();

	}

	@Override
	public void update() {
		
	}
	
	/**
	 * Set a {@link TextureObject} to use.
	 * @param texture The texture object
	 * @param resetCoords Reset coordinates using {@link #resetCoordinates()}
	 */
	public void setTexture(TextureObject texture, boolean resetCoords) {
		
		this.texture = texture;
		if ( resetCoords ) this.resetCoordinates();
		
	}
	
	/**
	 * 
	 * @param texture
	 */
	public void setTexture(TextureObject texture) {
		this.setTexture( texture, true );
	}
	
	/**
	 * Set a {@link Texture} to use.
	 * @param texture The texture
	 * @param resetCoords Reset coordinates using {@link #setTexture(TextureObject, boolean)}
	 * @see #setTexture(TextureObject, boolean)
	 */
	public void setTexture(Texture texture, boolean resetCoords) {
		this.setTexture( texture.getTextureObject(), resetCoords );
	}
	
	/**
	 * Set a {@link Texture} to use and reset coordinates.
	 * @param texture The texture
	 */
	public void setTexture(Texture texture) {
		this.setTexture( texture, true );
	}
	
	/**
	 * Set a {@link TextureMapTile} to use as texture.
	 * @param textureMapTile Texture tile to set
	 */
	public void setTexture(TextureMapTile textureMapTile) {
		
		this.setTexture( textureMapTile.map.getTextureObject(), false );
		
		this.textureX = textureMapTile.x;
		this.textureY = textureMapTile.y;
		this.textureWidth = textureMapTile.width;
		this.textureHeight = textureMapTile.height;
		
		this.updateTexCoords = true;
		
	}
	
	/**
	 * Reset coordinates : x and y to <code>0f</code> and width and height to <code>1f</code>.
	 */
	public void resetCoordinates() {
		
		this.textureX = 0f;
		this.textureY = 0f;
		this.textureWidth = 1f;
		this.textureHeight = 1f;
		
		this.updateTexCoords = true;
		
	}
	
	/**
	 * Set the texture coordinates.
	 * @param x X texture coordinate
	 * @param y Y texture coordinate
	 * @param width Texture width
	 * @param height Texture height
	 */
	public void setTextureCoords(float x, float y, float width, float height) {
		
		this.textureX = x;
		this.textureY = y;
		this.textureWidth = width;
		this.textureHeight = height;
		
		this.updateTexCoords = true;
		
	}

}
