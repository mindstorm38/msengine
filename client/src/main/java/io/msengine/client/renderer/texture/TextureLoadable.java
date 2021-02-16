package io.msengine.client.renderer.texture;

@Deprecated
public abstract class TextureLoadable extends Texture {

	protected final String path;
	
	public TextureLoadable(String path) {
		
		this.path = path;
		
	}
	
	public String getPath() {
		return this.path;
	}

}
