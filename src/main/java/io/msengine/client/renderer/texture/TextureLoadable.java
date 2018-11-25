package io.msengine.client.renderer.texture;

public abstract class TextureLoadable extends Texture {

	protected final String path;
	
	public TextureLoadable(String path) {
		
		this.path = path;
		
	}
	
	public String getPath() {
		return this.path;
	}

}
