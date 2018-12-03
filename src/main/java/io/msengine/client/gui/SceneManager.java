package io.msengine.client.gui;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.registry.NamespaceRegistry;

/**
 * 
 * A Scene Manager
 * 
 * @author Mindstorm38
 *
 */
public class SceneManager {
	
	// Singleton \\
	
	private static SceneManager INSTANCE;
	
	public SceneManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( SceneManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private final NamespaceRegistry<Class<? extends Scene>> scenes; 
	
	public SceneManager() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( SceneManager.class );
		INSTANCE = this;
		
		this.scenes = new NamespaceRegistry<>();
		
	}
	
	/**
	 * Register a scene class.
	 * @param identifier The scene identifier
	 * @param sceneClass
	 */
	public void registerScene(String identifier, Class<? extends Scene> sceneClass) {
		this.scenes.register( identifier, sceneClass );
	}
	
}
