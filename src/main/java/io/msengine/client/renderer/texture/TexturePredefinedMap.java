package io.msengine.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;

import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.sutil.StreamUtils;

/**
 * 
 * A predefined map of tiles.
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class TexturePredefinedMap extends TextureMapBase<TextureMapTile> {
	
	public static final Function<Integer, String> DEFAULT_FRAME_PATH_BUILDER = ( i ) -> "frame_" + i;
	
	public TexturePredefinedMap(String path) {
		
		super( path );
		
	}
	
	@Override
	public void loadTexture(ResourceManager resourceManager) throws IOException {
		
		this.delete();
		
		DetailledResource resource = null;
		
		try {
			
			resource = resourceManager.getDetailledResource( this.path );
			BufferedImage image = resource.getImage();
			
			this.texture = new TextureObject( image );
			
		} finally {
			
			StreamUtils.safeclose( resource );
			
		}
		
	}
	
	/**
	 * Register a new tile in this map with normalized coordinates.
	 * @param path The used to get this tile
	 * @param x X pos (top left)
	 * @param y Y pos (top left)
	 * @param width Width
	 * @param height Height
	 * @return The new registered tile
	 */
	public TextureMapTile newTile(String path, float x, float y, float width, float height) {
		TextureMapTile tile = new TextureMapTile( this, x, y, width, height );
		this.atlasTiles.put( path, tile );
		return tile;
	}
	
	/**
	 * Register a frame sequence in this map, using normalized coordinates.
	 * @param x Sequence starting position X (top left)
	 * @param y Sequence starting position Y (top left)
	 * @param frameWidth One frame width
	 * @param frameHeight One frame height
	 * @param frameCount Frame count to register
	 * @param pathBuilder A function that take the frame index and return its path (path is used to register the tile using {@link #newTile(String, float, float, float, float)})
	 * @return A list of <code>frameCount</code> map tiles
	 * @see #newTile(String, float, float, float, float)
	 */
	public TextureMapTile[] newFrameTiles(float x, float y, float frameWidth, float frameHeight, int frameCount, Function<Integer, String> pathBuilder) {
		
		if ( pathBuilder == null )
			pathBuilder = DEFAULT_FRAME_PATH_BUILDER;
		
		final TextureMapTile[] tiles = new TextureMapTile[ frameCount ];
		
		for ( int i = 0; i < frameCount; i++ ) {
			tiles[ i ] = this.newTile( pathBuilder.apply( i ), x + ( frameWidth * i ), y, frameWidth, frameHeight );
		}
		
		return tiles;
		
	}
	
}
