package io.msengine.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

import io.msengine.common.resource.ResourceManager;

public class DynamicTexture extends Texture {
	
	private final BufferedImage image;
	
	public DynamicTexture(int width, int height) {
		
		this.image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		this.texture = new TextureObject( width, height );
		
	}
	
	public DynamicTexture(BufferedImage image) {
		
		if ( image == null ) throw new NullPointerException("'image' parameter must not be null");
		
		this.image = image;
		this.texture = new TextureObject( image );
		
		this.updateTexture();
		
	}
	
	@Override
	public void loadTexture(ResourceManager resourceManager) throws IOException {}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public void updateTexture() {
		this.texture.upload( this.image );
	}
	
}
