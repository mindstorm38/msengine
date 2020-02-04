package io.msengine.client.renderer.font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.renderer.texture.DynamicTexture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.msengine.client.renderer.font.metadata.FontMetadataGlyph;
import io.msengine.client.renderer.font.metadata.FontMetadataSection;
import io.msengine.common.resource.metadata.Metadata;
import io.msengine.common.resource.metadata.MetadataSerializer;

public class FontHandler implements ShaderSamplerObject {
	
	static {
		
		if (!MetadataSerializer.getInstance().hasMetadataSectionType(FontMetadataSection.class))
			MetadataSerializer.getInstance().registerMetadataSectionType(new FontMetadataSection.Serializer(), FontMetadataSection.class);

	}
	
	// Class \\
	
	private final String path;
	private final Map<Character, FontHandlerGlyph> glyphs;
	private FontMetadataSection resourceMetadataSection;
	private DynamicTexture texture;
	private int height;
	private int underlineOffset;
	private float textureHeight;
	private FontHandlerGlyph unknownGlyph;
	
	public FontHandler(String path) {
		
		this.path = path;
		this.glyphs = new HashMap<>();
		
	}
	
	public void load() throws Exception {
		
		try ( DetailledResource resource = ResourceManager.getInstance().getDetailledResource( this.path ) ) {
			
			if ( !resource.hasMetadata() ) {
				throw new IllegalStateException("Invalid path given, no metadata");
			} else {
				
				Metadata resourceMetadata = resource.getMetadata();
				
				this.resourceMetadataSection = resourceMetadata.getMetadataSection("font");
				
				if ( this.resourceMetadataSection == null )
					throw new IllegalStateException("Invalid or not found FontMetadataSection");
				
			}
			
			this.glyphs.clear();
			
			BufferedImage image = resource.getImage();
			resource.close();
			
			if (image == null)
				throw new IllegalStateException("Failed to load image !");
			
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			
			this.height = this.resourceMetadataSection.getHeight();
			this.underlineOffset = this.resourceMetadataSection.getUnderlineOffset();
			this.textureHeight = (float) this.height / imageHeight;
			
			for ( Entry<Character, FontMetadataGlyph> glyphEntry : this.resourceMetadataSection.getGlyphs().entrySet() ) {
				
				FontMetadataGlyph glyph = glyphEntry.getValue();
				
				float x = (float) glyph.x / imageWidth;
				float y = (float) glyph.y / imageHeight;
				float width = (float) glyph.width / imageWidth;
				
				FontHandlerGlyph handlerGlyph = new FontHandlerGlyph( this, glyph.x, glyph.y, glyph.width, x, y, width );
				
				if ( glyphEntry.getKey() == null ) {
					this.unknownGlyph = handlerGlyph;
				} else {
					this.glyphs.put( glyphEntry.getKey(), handlerGlyph );
				}
				
			}
			
			this.texture = new DynamicTexture( image );
			TextureManager.getInstance().loadTexture( this.texture );
			
		}
		
	}
	
	public String getPath() {
		return this.path;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getUnderlineOffset() {
		return underlineOffset;
	}
	
	public float getTextureHeight() {
		return this.textureHeight;
	}
	
	public FontHandlerGlyph getCharacterGlyph(char c) {
		return this.glyphs.containsKey( c ) ? this.glyphs.get( c ) : this.unknownGlyph;
	}
	
	public Set<Character> getAvailableCharacters() {
		return this.glyphs.keySet();
	}
	
	public void bindTexture() {
		this.texture.bind();
	}
	
	public void bindTexture(int active) {
		this.texture.bind( active );
	}
	
	public boolean isCharSupported(char ch) {
		return this.glyphs.containsKey( ch );
	}
	
	public void saveImage(File folder, String name) throws IOException {
		
		if ( this.texture == null ) throw new IllegalStateException("Texture not initied");
		
		File file = new File( folder, name + ".png" );
		ImageIO.write( this.texture.getImage(), "png", file );
		
	}

	@Override
	public int getSamplerId() {
		return this.texture.getSamplerId();
	}
	
}
