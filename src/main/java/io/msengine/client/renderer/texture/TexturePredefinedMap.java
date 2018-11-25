package io.msengine.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.sutil.StreamUtils;

public class TexturePredefinedMap extends TextureMapBase {
	
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
	
	public TextureMapTile newTile(String path, float x, float y, float width, float height) {
		TextureMapTile tile = new TextureMapTile( this, x, y, width, height );
		this.atlasTiles.put( path, tile );
		return tile;
	}
	
}
