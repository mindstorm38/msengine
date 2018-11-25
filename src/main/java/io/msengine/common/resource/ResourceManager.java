package io.msengine.common.resource;

import java.util.ArrayList;
import java.util.List;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.resource.Resource;
import io.sutil.resource.ResourceAccessorWrapper;

public class ResourceManager extends ResourceAccessorWrapper {
	
	// Singleton \\
	
	private static ResourceManager INSTANCE = null;
	
	public static ResourceManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( ResourceManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	public ResourceManager(String baseFolderPath) {
		
		super( ResourceManager.class, baseFolderPath );
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( ResourceManager.class );
		INSTANCE = this;
		
	}
	
	@SuppressWarnings("resource")
	public DetailledResource getDetailledResource(String path) {
		
		Resource resource = this.getResource( path );
		return resource == null ? null : new DetailledResource( resource );
		
	}
	
	public List<DetailledResource> listDetailledResources(String path) {
		List<DetailledResource> resources = new ArrayList<>();
		for ( Resource resource : this.listResources( path ) ) resources.add( new DetailledResource( resource ) );
		return resources;
	}
	
	// Static \\
	
	public static String getResourceMetaPath(String path) {
		return path + ".meta";
	}

}
