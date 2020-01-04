package io.msengine.client.renderer.texture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.msengine.client.renderer.util.RenderConstantFields;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;

public class TextureManager {
	
	// Constants \\
	
	public static final float TEXTURE_EDGES = 0.02f; // In pixel
	
	// Singleton \\
	
	private static TextureManager INSTANCE = null;
	
	public static TextureManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( TextureManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private final ResourceManager resourceManager;
	private final Map<String, Texture> loadableTextures; // Loadable textures mapped to their paths
	private final List<Texture> textures; // All others textures
	
	public TextureManager() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( TextureManager.class );
		INSTANCE = this;
		
		this.resourceManager = ResourceManager.getInstance();
		this.loadableTextures = new HashMap<>();
		this.textures = new ArrayList<>();
		
	}
	
	public void loadTexture(Texture texture) {
		
		boolean loadable = isLoadable( texture );
		
		try {
			
			texture.loadTexture( this.resourceManager );
			
		} catch (IOException e) {
			
			texture = RenderConstantFields.getInstance().getMissingTexture();
			
		}
		
		if ( loadable ) {
			this.loadableTextures.put( ( (TextureLoadable) texture ).path, texture );
		} else {
			this.textures.add( texture );
		}
		
	}
	
	public Texture getTexture(String path) {
		return this.loadableTextures.get( path );
	}
	
	public static boolean isLoadable(Texture texture) {
		return texture instanceof TextureLoadable;
	}
	
}
