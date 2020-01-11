package io.msengine.client.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.system.MemoryUtil;

import io.msengine.client.game.RenderGame;
import io.msengine.client.renderer.font.FontHandler;
import io.msengine.client.renderer.font.FontHandlerGlyph;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

import static io.msengine.client.renderer.vertex.type.GuiFormat.*;

public class GuiTextBase extends GuiObject {
	
	protected IndicesDrawBuffer buffer;
	protected boolean updateBuffer;

	protected FontHandler font;
	protected String text;
	protected char[] textChars;
	protected float[] charsOffsets;
	protected float charSpacing;
	
	public GuiTextBase(FontHandler font, String text) {

		this.setFont( font );
		this.setText( text );
		
		this.charSpacing = 1f;
		
	}
	
	public GuiTextBase(FontHandler font) {
		this( font, "" );
	}
	
	public GuiTextBase(String text) {
		this( RenderGame.getCurrentRender().getDefaultFont(), text );
	}
	
	public GuiTextBase() {
		this("");
	}
	
	@Override
	public void init() {
		
		this.buffer = this.renderer.createDrawBuffer( false, true );
		
		this.updateTextBuffers();
		
	}

	@Override
	public void stop() {
		
		this.buffer.delete();
		this.buffer = null;
		
	}
	
	protected void updateTextBuffers() {
		
		int length = this.textChars.length;
		int height = this.font.getHeight();
		
		float textureHeight = this.font.getTextureHeight();
		
		float x = 0f;
		
		FloatBuffer verticesBuffer = null;
		FloatBuffer texCoordsBuffer = null;
		IntBuffer indicesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat( length * 12 );
			texCoordsBuffer = MemoryUtil.memAllocFloat( length * 8 );
			indicesBuffer = MemoryUtil.memAllocInt( this.buffer.setIndicesCount( length * 6 ) );
			
			for ( int i = 0; i < length; i++ ) {
				
				FontHandlerGlyph glyph = this.font.getCharacterGlyph( this.textChars[i] );
				
				if ( glyph != null ) {
					
					verticesBuffer.put( x ).put( 0 );
					verticesBuffer.put( x ).put( height );
					verticesBuffer.put( x + glyph.width ).put( height );
					verticesBuffer.put( x + glyph.width ).put( 0 );
					
					texCoordsBuffer.put( glyph.textureX ).put( glyph.textureY );
					texCoordsBuffer.put( glyph.textureX ).put( glyph.textureY + textureHeight );
					texCoordsBuffer.put( glyph.textureX + glyph.textureWidth ).put( glyph.textureY + textureHeight );
					texCoordsBuffer.put( glyph.textureX + glyph.textureWidth ).put( glyph.textureY );
					
					int idx = i * 4;
					
					indicesBuffer.put( idx ).put( idx + 1 ).put( idx + 3 );
					indicesBuffer.put( idx + 1 ).put( idx + 2 ).put( idx + 3 );
					
					x += glyph.width + this.charSpacing;
					this.charsOffsets[ i ] = x;
					
				}
				
			}
			
			if ( x > 0 )
				x -= this.charSpacing;
			
			verticesBuffer.flip();
			texCoordsBuffer.flip();
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboData( GUI_POSITION, verticesBuffer, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadVboData( GUI_TEX_COORD, texCoordsBuffer, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadIboData( indicesBuffer, BufferUsage.DYNAMIC_DRAW );
			
		} finally {
			
			if ( verticesBuffer != null ) MemoryUtil.memFree( verticesBuffer );
			if ( texCoordsBuffer != null ) MemoryUtil.memFree( texCoordsBuffer );
			if ( indicesBuffer != null ) MemoryUtil.memFree( indicesBuffer );
			
		}
		
		this.updateBuffer = false;
		
		this.width = x;
		this.updateXOffset();
		
	}
	
	@Override
	public void setWidth(float width) {}
	
	@Override
	public void setHeight(float height) {}
	
	@Override
	public void render(float alpha) {
		
		if ( this.updateBuffer ) {
			
			this.updateTextBuffers();
			
		}
		
		this.renderText( alpha );
		
	}

	public void renderText(float alpha) {
		
		this.renderer.setTextureSampler( this.font );
			
			this.model.push().scale( 2f ).translate( this.xOffset, this.yOffset ).apply();
			
				this.buffer.drawElements();
			
			this.model.pop();
			
		this.renderer.resetTextureSampler();
		
	}
	
	@Override
	public void update() {}
	
	/**
	 * @return The font used to render this text
	 */
	public FontHandler getFont() {
		return this.font;
	}
	
	/**
	 * Set the font used to render this text.
	 * @param font The font used
	 * @return <code>true</code> if this has an effect
	 */
	public boolean setFont(FontHandler font) {
		
		Objects.requireNonNull( font, "Font can't be null" );
		
		if ( font.equals( this.font ) ) return false;
		
		this.font = font;
		this.updateBuffer = true;
		
		this.height = font.getHeight();
		this.updateYOffset();
		
		return true;
		
	}
	
	/**
	 * Set the text to show.
	 * @param text Text to set
	 * @return <code>true</code> if this has an effect
	 */
	public boolean setText(String text) {
		
		Objects.requireNonNull( text, "Text can't be null" );
		
		if ( text.equals( this.text ) ) return false;
		
		this.text = text;
		this.textChars = text.toCharArray();
		this.charsOffsets = new float[ this.textChars.length ];
		this.updateBuffer = true;
		
		return true;
		
	}
	
	/**
	 * @return Space between consecutive characters.
	 */
	public float getCharSpacing() {
		return this.charSpacing;
	}
	
	/**
	 * Set the space between consecutive characters.
	 * @param charSpacing The space
	 */
	public void setCharSpacing(float charSpacing) {
		this.charSpacing = charSpacing;
	}
	
	/**
	 * Get a char x offset in the text.
	 * @param index The char index in the text string
	 * @return The x offset
	 */
	public float getCharOffset(int index) {
		
		if ( this.charsOffsets.length == 0 || index < 0 )
			return 0f;
		else if ( index >= this.textChars.length - 1 )
			index = this.textChars.length - 1;
		
		return this.charsOffsets[ index ];
		
	}

}
