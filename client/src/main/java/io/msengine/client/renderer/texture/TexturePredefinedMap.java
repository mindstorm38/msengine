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
@Deprecated
public class TexturePredefinedMap extends TextureMapBase<TextureMapTile> {
	
	public static final Function<Integer, String> DEFAULT_FRAME_PATH_BUILDER = ( i ) -> "frame_" + i;
	
	private int imageWidth;
	private int imageHeight;
	
	/**
	 * Construct a predefined map.
	 * @param path Image path in resource system.
	 * @param imageWidth The image width, this property is <b>ONLY</b> used as utility to add tile using pixel coordinates.
	 * @param imageHeight The image height, this property is <b>ONLY</b> used as utility to add tile using pixel coordinates.
	 */
	public TexturePredefinedMap(String path, int imageWidth, int imageHeight) {
		
		super(path);
		
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		
	}
	
	/**
	 * Construct a predefined map.
	 * @param path Image path in resource system.
	 */
	public TexturePredefinedMap(String path) {
		this(path, 0, 0);
	}
	
	@Override
	public void loadTexture(ResourceManager resourceManager) {
		
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
	 * @param path The tile path, or identifier.
	 * @param x X pos (top left).
	 * @param y Y pos (top left).
	 * @param width Width.
	 * @param height Height.
	 * @return The new registered tile.
	 */
	public TextureMapTile newTile(String path, float x, float y, float width, float height) {
		TextureMapTile tile = new TextureMapTile( this, x, y, width, height );
		this.atlasTiles.put( path, tile );
		return tile;
	}
	
	/**
	 * Register a new tile in this map with pixel coordinates (not normalized), this method use image
	 * width and height defined using {@link #setImageWidth(int)} and {@link #setImageHeight(int)} to
	 * transform given pixel coordinates to normalized ones.
	 * @param path The tile path, or identifier.
	 * @param x X pos (top left).
	 * @param y Y pos (top left).
	 * @param width Width.
	 * @param height Height.
	 * @return The new registered tile.
	 */
	public TextureMapTile newPixelTile(String path, int x, int y, int width, int height) {
		float iw = (float) this.imageWidth;
		float ih = (float) this.imageHeight;
		return this.newTile(path, x / iw, y / ih, width / iw, height / ih);
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
	
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	
}
