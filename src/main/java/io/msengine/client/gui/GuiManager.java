package io.msengine.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.registry.NamespaceRegistry;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * A Scene Manager
 * 
 * @author Mindstorm38
 *
 */
public class GuiManager {
	
	// Singleton \\
	
	private static GuiManager INSTANCE;
	
	public static GuiManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( GuiManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private final NamespaceRegistry<Class<? extends GuiScene>> scenes; 
	private final Map<Class<? extends GuiScene>, GuiScene> instances;
	
	private GuiScene currentScene;
	
	public GuiManager() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( GuiManager.class );
		INSTANCE = this;
		
		this.scenes = new NamespaceRegistry<>();
		this.instances = new HashMap<>();
		
	}
	
	/**
	 * Register a scene class.
	 * @param identifier The scene identifier
	 * @param sceneClass The class of the scene, the scene constructor must be empty
	 */
	public void registerSceneClass(String identifier, Class<? extends GuiScene> sceneClass) {
		this.scenes.register( identifier, sceneClass );
	}
	
	/**
	 * Internal method to get scene instance.
	 * @param sceneClass The scene class
	 * @return A cached scene instance, or a newly created one if none is cached
	 */
	private GuiScene getSceneInstance(Class<? extends GuiScene> sceneClass) {
		
		if ( sceneClass == null ) return null;
		
		GuiScene scene = this.instances.get( sceneClass );
		if ( scene != null ) return scene;
		
		try {
			
			scene = sceneClass.newInstance();
			
		} catch (InstantiationException | IllegalAccessException e) {
			
			LOGGER.log( Level.SEVERE, "Failed to instantiate the scene " + sceneClass.getSimpleName(), e );
			return null;
			
		}
		
		this.instances.put( sceneClass, scene );
		return scene;
		
	}
	
	/**
	 * Load a scene from its class.
	 * @param sceneClass The scene class
	 * @param oncePreviousStoped An optional consumer launched, if a scene is already loaded, after this previous scene {@link GuiScene#stop()} call, the consumer parameter is this last scene instance
	 */
	public void loadScene(Class<? extends GuiScene> sceneClass, Consumer<GuiScene> oncePreviousStoped) {
		
		GuiScene inst = this.getSceneInstance( sceneClass );
		if ( this.currentScene == inst ) return;
		
		if ( this.currentScene != null ) {
			
			this.currentScene.stop();
			
			if ( oncePreviousStoped != null )
				oncePreviousStoped.accept( this.currentScene );
			
		}
		
		this.currentScene = inst;
		
		if ( inst != null ) {
			
			inst.init();
			
		}
		
	}
	
	/**
	 * Load a scene from its class.
	 * @param sceneClass The scene class
	 */
	public void loadScene(Class<? extends GuiScene> sceneClass) {
		this.loadScene( sceneClass, null );
	}
	
	/**
	 * Load a scene from its registered identifier.
	 * @param sceneIdentifier The scene identifier
	 * @param oncePreviousStoped See {@link #loadScene(Class, Consumer)}
	 * @see #loadScene(Class, Consumer)
	 */
	public void loadScene(String sceneIdentifier, Consumer<GuiScene> oncePreviousStoped) {
		
		if ( sceneIdentifier == null ) {
			
			this.loadScene( (Class<? extends GuiScene>) null, oncePreviousStoped );
			
		} else {
			
			Class<? extends GuiScene> sceneClass = this.scenes.get( sceneIdentifier);
			if ( sceneClass == null ) throw new IllegalArgumentException("Invalid scene identifier");
			this.loadScene( sceneClass, oncePreviousStoped );
			
		}
		
	}
	
	/**
	 * Load a scene from its registered identifier.
	 * @param sceneIdentifier The scene identifier
	 * @see #loadScene(String, Consumer)
	 */
	public void loadScene(String sceneIdentifier) {
		this.loadScene( sceneIdentifier, null );
	}
	
	/**
	 * Unload the current loaded class
	 */
	public void unloadScene() {
		this.loadScene( (Class<? extends GuiScene>) null );
	}
	
	/**
	 * Uncache a scene instance from internal map, this can be passed in <code>oncePreviousStoped</code> parameter in {@link #loadScene(Class, Consumer)}.
	 * @param sceneClass The scene class to uncache
	 */
	public void uncacheScene(Class<? extends GuiScene> sceneClass) {
		this.instances.remove( sceneClass );
	}
	
	/**
	 * Unload current scene and clear all instances cache.
	 */
	public void stop() {
		
		this.unloadScene();
		this.instances.clear();
		
	}
	
	/**
	 * Render the current scene, or nothing if no scene is loaded.
	 * @param alpha Partials ticks
	 */
	public void render(float alpha) {
		
		if ( this.currentScene != null )
			this.currentScene.render( alpha );
		
	}
	
	/**
	 * Update the current scene, or nothing if no scene is loaded.
	 */
	public void update() {
		
		if ( this.currentScene != null )
			this.currentScene.update();
		
	}

}
