package io.msengine.client.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import io.msengine.client.renderer.util.BufferUtils;
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
	protected float textScale;
	protected boolean ignoreUnderline;
	
	public GuiTextBase(FontHandler font, String text) {

		this.setFont(font);
		this.setText(text);
		
		this.setCharSpacing(1f);
		this.setTextScale(1f);
		
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
		
		float textureHeight = this.font.getTextureHeight();
		float scale = this.textScale;
		float scaledCharSpacing = this.charSpacing * scale;
		
		float height = this.height;
		
		if (this.ignoreUnderline)
			height += scale * this.font.getUnderlineOffset();
		
		float x = 0f;
		
		FloatBuffer verticesBuffer = null;
		FloatBuffer texCoordsBuffer = null;
		IntBuffer indicesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat(length * 12);
			texCoordsBuffer = MemoryUtil.memAllocFloat(length * 8);
			indicesBuffer = MemoryUtil.memAllocInt(this.buffer.setIndicesCount(length * 6));
			
			for (int i = 0; i < length; ++i) {
				
				FontHandlerGlyph glyph = this.font.getCharacterGlyph(this.textChars[i]);
				
				if (glyph != null) {
					
					verticesBuffer.put( x ).put( 0 );
					verticesBuffer.put( x ).put( height );
					verticesBuffer.put( x + (glyph.width * scale) ).put( height );
					verticesBuffer.put( x + (glyph.width * scale) ).put( 0 );
					
					texCoordsBuffer.put( glyph.textureX ).put( glyph.textureY );
					texCoordsBuffer.put( glyph.textureX ).put( glyph.textureY + textureHeight );
					texCoordsBuffer.put( glyph.textureX + glyph.textureWidth ).put( glyph.textureY + textureHeight );
					texCoordsBuffer.put( glyph.textureX + glyph.textureWidth ).put( glyph.textureY );
					
					int idx = i * 4;
					
					indicesBuffer.put( idx ).put( idx + 1 ).put( idx + 3 );
					indicesBuffer.put( idx + 1 ).put( idx + 2 ).put( idx + 3 );
					
					x += glyph.width * scale;
					this.charsOffsets[i] = x;
					
					x += scaledCharSpacing;
					
				}
				
			}
			
			if ( x > 0 )
				x -= scaledCharSpacing;
			
			verticesBuffer.flip();
			texCoordsBuffer.flip();
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboData( GUI_POSITION, verticesBuffer, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadVboData( GUI_TEX_COORD, texCoordsBuffer, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadIboData( indicesBuffer, BufferUsage.DYNAMIC_DRAW );
			
		} finally {
			
			BufferUtils.safeFree(verticesBuffer);
			BufferUtils.safeFree(texCoordsBuffer);
			BufferUtils.safeFree(indicesBuffer);
			
		}
		
		this.updateBuffer = false;
		
		super.setWidth(x);
		
	}
	
	@Override
	public void setWidth(float width) {
		// Can't set width for text : the width is set from text size
		throw new UnsupportedOperationException("Can't set width for GuiTextBase objects.");
	}
	
	@Override
	public void setHeight(float height) {
		
		// Set height of GuiTextBase cause automatic calculation of text scale
		
		if (this.height == height)
			return;
		
		float fontHeight = this.font.getHeight();
		
		if (this.ignoreUnderline)
			fontHeight -= this.font.getUnderlineOffset();
		
		this.textScale = height / fontHeight;
		super.setHeight(height);
		this.updateBuffer = true;
		
	}
	
	@Override
	public void render(float alpha) {
		
		if (this.updateBuffer)
			this.updateTextBuffers();
		
		this.renderText(alpha);
		
	}

	public void renderText(float alpha) {
		
		this.renderer.setTextureSampler(this.font);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
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
		
		this.updateTextHeight();
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
	 * @return The current of this object.
	 */
	public String getText() {
		return this.text;
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
		
		if (this.charSpacing == charSpacing)
			return;
		
		this.charSpacing = charSpacing;
		this.updateBuffer = true;
		
	}
	
	/**
	 * @return Text scale (multiplier of default font size), default to 2.
	 */
	public float getTextScale() {
		return this.textScale;
	}
	
	/**
	 * Set the text scale (multiplier of default font size).
	 * @param scale Text scale
	 */
	public void setTextScale(float scale) {
		
		if (this.textScale == scale)
			return;
		
		this.textScale = scale;
		this.updateTextHeight();
		this.updateBuffer = true;
		
	}
	
	public boolean isIgnoreUnderline() {
		return ignoreUnderline;
	}
	
	public void setIgnoreUnderline(boolean ignoreUnderline) {
		
		if (this.ignoreUnderline == ignoreUnderline)
			return;
		
		this.ignoreUnderline = ignoreUnderline;
		this.updateTextHeight();
		this.updateBuffer = true;
		
	}
	
	/**
	 * Update text height, using font height and text scale.
	 */
	public void updateTextHeight() {
		
		float height = this.font.getHeight();
		
		if (this.ignoreUnderline)
			height -= this.font.getUnderlineOffset();
		
		super.setHeight(height * this.textScale);
		
	}
	
	/**
	 * @return The underline offset scaled.
	 */
	public float getScaledUnderlineOffset() {
		return this.textScale * this.font.getUnderlineOffset();
	}
	
	/**
	 * Get a char x offset in the text.
	 * @param index The char index in the text string
	 * @return The x offset
	 */
	public float getCharOffset(int index) {
		
		if ( this.charsOffsets.length == 0 || index < 0 )
			return -this.charSpacing * this.textScale; // Act like if there is a character at -1
		else if ( index >= this.textChars.length )
			index = this.textChars.length - 1;
		
		return this.charsOffsets[ index ];
		
	}

}
