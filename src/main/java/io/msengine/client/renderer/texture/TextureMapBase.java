package io.msengine.client.renderer.texture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class TextureMapBase extends TextureLoadable {
	
	protected final Map<String, TextureMapTile> atlasTiles;
	
	public TextureMapBase(String path) {
		
		super( path );
		
		this.atlasTiles = new HashMap<>();
		
	}
	
	public TextureMapTile getTile(String path) {
		return this.atlasTiles.get( path );
	}
	
	public Map<String, TextureMapTile> getTiles() {
		return Collections.unmodifiableMap( this.atlasTiles );
	}
	
	public String getPath() {
		return this.path;
	}
	
}
