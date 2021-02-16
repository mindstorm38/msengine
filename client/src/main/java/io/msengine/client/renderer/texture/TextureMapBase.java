package io.msengine.client.renderer.texture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public abstract class TextureMapBase<T extends TextureMapTile> extends TextureLoadable {
	
	protected final Map<String, T> atlasTiles;
	
	public TextureMapBase(String path) {
		
		super( path );
		
		this.atlasTiles = new HashMap<>();
		
	}
	
	public T getTile(String path) {
		return this.atlasTiles.get( path );
	}
	
	public Map<String, T> getTiles() {
		return Collections.unmodifiableMap( this.atlasTiles );
	}
	
	public String getPath() {
		return this.path;
	}
	
}
