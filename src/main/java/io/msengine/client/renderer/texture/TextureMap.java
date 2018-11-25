package io.msengine.client.renderer.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import io.msengine.client.renderer.util.RenderConstantFields;
import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.sutil.StreamUtils;

import static io.msengine.common.util.GameLogger.LOGGER;

public class TextureMap extends TextureMapBase {
	
	// Constants \\
	
	public static final boolean SAVE_ATLAS = true;
	
	public static final Function<String, String> PNG_FILTER = path -> {
		return path.endsWith(".png") ? path.substring( 0, path.length() - 4 ) : null;
	};
	
	// Class \\
	
	private final Function<String, String> filter;
	
	public TextureMap(String path, Function<String, String> filter) {
		
		super( path );
		
		this.filter = filter;
		
	}
	
	@Override
	public void loadTexture(ResourceManager resourceManager) throws IOException {
		
		this.delete();
		
		this.atlasTiles.clear();
		
		// Retrieving all texture images
		List<DetailledResource> resources = resourceManager.listDetailledResources( this.path );
		
		Map<String, BufferedImage> images = new HashMap<>();
		
		int maxTileWidth = 0;
		int maxTileHeight = 0;
		
		for ( DetailledResource res : resources ) {
			
			String path = res.getName();
			
			if ( this.filter != null )
				if ( ( path = this.filter.apply( path ) ) == null )
					continue;
			
			BufferedImage bi = res.getImage();
			
			StreamUtils.safeclose( res );
			
			if ( bi == null ) {
				
				LOGGER.warning( "Unable to load texture for a map at '" + path + "'" );
				
			} else {
				
				int biWidth = bi.getWidth();
				int biHeight = bi.getHeight();
				
				if ( biWidth > maxTileWidth ) maxTileWidth = biWidth;
				if ( biHeight > maxTileHeight ) maxTileHeight = biHeight;
				
			}
			
			images.put( path, bi );
				
		}
		
		if ( maxTileWidth == 0 ) maxTileWidth = 1;
		if ( maxTileHeight == 0 ) maxTileHeight = 1;
		
		maxTileWidth += 2;
		maxTileHeight += 2;
		
		// Missing texture
		BufferedImage missingTextureImgRaw = RenderConstantFields.getInstance().getMissingTexture().getImage();
		BufferedImage missingTextureImg = new BufferedImage( maxTileWidth, maxTileHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D missingTextureGraphics = missingTextureImg.createGraphics();
		missingTextureGraphics.drawImage( missingTextureImgRaw, 0, 0, maxTileWidth - 2, maxTileHeight - 2, null );
		missingTextureGraphics.dispose();
		images.put( null, missingTextureImg );
		
		int imageSize = (int) Math.ceil( Math.sqrt( images.size() ) );
		int imageWidth = imageSize * maxTileWidth;
		int imageHeight = imageSize * maxTileHeight;
		
		// Drawing atlas
		int maxTextureSize = RenderConstantFields.getInstance().getMaxTextureSize();
		
		if ( imageWidth > maxTextureSize || imageHeight > maxTextureSize ) {
			throw new IOException( "Wanted texture size is to large (" + maxTextureSize + ")" );
		}
		
		BufferedImage atlas = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D atlasGraphics = atlas.createGraphics();
		
		int column = 0;
		int row = 0;
		
		for ( Entry<String, BufferedImage> imageEntry : images.entrySet() ) {
			
			if ( imageEntry.getValue() != null ) {
				
				BufferedImage bi = imageEntry.getValue();
				
				int x = column * maxTileWidth + 1;
				int y = row * maxTileHeight + 1;
				int width = bi.getWidth();
				int height = bi.getHeight();
				
				atlasGraphics.drawImage( bi, x, y, null ); // Center
				atlasGraphics.drawImage( bi, x - 1, y, x, y + height, 0, 0, 1, height, null ); // Left
				atlasGraphics.drawImage( bi, x, y + height, x + width, y + height + 1, 0, height - 1, width, height, null ); // Bottom
				atlasGraphics.drawImage( bi, x + width, y, x + width + 1, y + height, width - 1, 0, width, height, null ); // Right
				atlasGraphics.drawImage( bi, x, y - 1, x + width, y, 0, 0, width, 1, null ); // Top
				
				TextureMapTile tile = new TextureMapTile( this, (float) x / imageWidth, (float) y / imageHeight, (float) width / imageWidth, (float) height / imageHeight );
				this.atlasTiles.put( imageEntry.getKey(), tile );
				
			}
			
			if ( column >= ( imageSize - 1 ) ) {
				column = 0;
				row++;
			} else {
				column++;
			}
			
		}
		
		atlasGraphics.dispose();
		
		/*
		if ( SAVE_ATLAS ) {
			
			File debugFile = new File( Game.getInstance().getAppData(), FileUtils.getFileName( this.path ) + "_atlas.png" );
			ImageIO.write( atlas, "png", debugFile );
			
		}
		*/
		
		this.texture = new TextureObject( atlas );
		
	}
	
	@Override
	public TextureMapTile getTile(String path) {
		return this.atlasTiles.containsKey( path ) ? this.atlasTiles.get( path ) : this.atlasTiles.get( null );
	}
	
}
