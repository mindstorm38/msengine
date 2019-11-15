package io.msengine.common.resource;

import java.util.ArrayList;
import java.util.List;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.resource.Resource;
import io.sutil.resource.ResourceAccessor;

public class ResourceManager extends io.sutil.resource.ResourceManager {
	
	// Singleton \\
	
	private static ResourceManager INSTANCE = null;
	
	public static ResourceManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( ResourceManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	public ResourceManager(Class<?> runningClass, String baseDirectoryPath, String mainNamespace) {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( ResourceManager.class );
		INSTANCE = this;
		
		try {
			
			this.registerAccessor(mainNamespace, ResourceAccessor.findClassResourceAccessor(runningClass, baseDirectoryPath));
			this.registerAccessor("msengine", ResourceAccessor.findClassResourceAccessor(ResourceManager.class, baseDirectoryPath));
			
		} catch (IllegalStateException e) {
			throw new IllegalStateException( "Unable to create the ResourceManager", e );
		}
		
	}
	
	@SuppressWarnings("resource")
	public DetailledResource getDetailledResource(String path) {
		
		Resource resource = this.getResource( path );
		return resource == null ? null : new DetailledResource( resource );
		
	}
	
	public List<DetailledResource> listDetailledResources(String path) {
		final List<Resource> rawResources = this.listResources( path );
		if ( rawResources == null ) return null;
		final List<DetailledResource> resources = new ArrayList<>();
		for ( Resource resource : rawResources ) resources.add( new DetailledResource( resource ) );
		return resources;
	}
	
	// Static \\
	
	public static String getResourceMetaPath(String path) {
		return path + ".meta";
	}

}
