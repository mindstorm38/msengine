package io.msengine.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import io.msengine.client.gui.event.GuiSceneResizedEvent;
import io.msengine.client.renderer.gui.GuiRenderer;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.registry.NamespaceRegistry;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * A Scene Manager
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 * TODO Add automatic scene uncaching after a while.
 *
 */
public class GuiManager implements WindowFramebufferSizeEventListener {
	
	// Singleton \\
	
	private static GuiManager INSTANCE;
	
	public static GuiManager getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( GuiManager.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private final Window window;
	private final GuiRenderer renderer;
	
	private final NamespaceRegistry<Class<? extends GuiScene>> scenes; 
	private final Map<Class<? extends GuiScene>, GuiScene> instances;
	
	private GuiScene currentScene;
	
	public GuiManager() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( GuiManager.class );
		INSTANCE = this;
		
		this.window = Window.getInstance();
		this.renderer = new GuiRenderer();
		
		this.scenes = new NamespaceRegistry<>();
		this.instances = new HashMap<>();
		
	}
	
	/**
	 * @return The {@link GuiRenderer} singleton instance
	 */
	public GuiRenderer getRenderer() {
		return this.renderer;
	}
	
	/**
	 * Init internal {@link GuiRenderer}
	 */
	public void init() {

		this.renderer.init();
		
		this.updateRenderSize( this.window );
		this.window.addFramebufferSizeEventListener( this );
		
	}
	
	/**
	 * Unload current scene and clear all instances cache.
	 */
	public void stop() {
		
		this.window.removeFramebufferSizeEventListener( this );
		
		this.unloadScene();
		
		this.instances.values().forEach(GuiScene::_stop);
		this.instances.clear();
		
		this.renderer.stop();
		
	}
	
	/**
	 * Render the current scene, or nothing if no scene is loaded.
	 * @param alpha Partials ticks
	 */
	public void render(float alpha) {
		
		if ( this.currentScene != null ) {
			
			this.renderer.beginRender();
			
			this.currentScene.render( alpha );
			
			this.renderer.endRender();
			
		}
		
	}
	
	/**
	 * Update the current scene, or nothing if no scene is loaded.
	 */
	public void update() {
		
		if ( this.currentScene != null )
			this.currentScene.update();
		
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
		
		GuiScene scene = this.instances.get(sceneClass);
		
		if (scene != null)
			return scene;
		
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
		
		if (this.currentScene == inst)
			return;
		
		if (this.currentScene != null) {
			
			this.currentScene.unloaded();
			
			if (oncePreviousStoped != null)
				oncePreviousStoped.accept(this.currentScene);
			
		}
		
		this.currentScene = inst;
		
		if ( inst != null ) {
			
			if (!inst.usable()) {
				inst._init();
			}
			
			inst.loaded();
			inst.fireEvent(new GuiSceneResizedEvent(this.window));
			
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
		
		GuiScene oldScene = this.instances.remove(sceneClass);
		
		if (oldScene != null && oldScene.usable()) {
			oldScene._stop();
		}
		
	}
	
	/**
	 * Internal method to update render size.
	 * @param width The new width used to render
	 * @param height The new height used to render
	 */
	private void updateRenderSize(int width, int height) {
		
		this.renderer.updateRenderSize( width, height );
		
		if ( this.currentScene != null ) {
			this.currentScene.fireEvent(new GuiSceneResizedEvent(width, height));
		}
		
	}
	
	/**
	 * Internal mathod to update render size usinga {@link Window} instance.
	 * @param window The window instance
	 */
	private void updateRenderSize(Window window) {
		this.updateRenderSize( window.getWidth(), window.getHeight() );
	}

	@Override
	public void windowFramebufferSizeChangedEvent(int width, int height) {
		this.updateRenderSize( width, height );
	}
	
}
