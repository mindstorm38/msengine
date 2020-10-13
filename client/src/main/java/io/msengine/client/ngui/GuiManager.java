package io.msengine.client.ngui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class GuiManager {

    private static final Logger LOGGER = Logger.getLogger("msengine.gui");

    private final Map<String, Supplier<GuiScene>> scenes = new HashMap<>();
    private final Map<String, GuiScene> instances = new HashMap<>();

    private GuiScene currentScene;

    /**
     * Register a scene class.
     * @param identifier The scene identifier.
     * @param provider The scene provider.
     */
    public void registerScene(String identifier, Supplier<GuiScene> provider) {
        this.scenes.put(identifier, provider);
    }

    /**
     * Internal method to get scene instance.
     * @param identifier The scene identifier.
     * @return A cached scene instance, or a newly created one if none is cached.
     */
    private GuiScene getSceneInstance(String identifier) {

        if (identifier == null)
            return null;

        GuiScene scene = this.instances.get(identifier);

        if (scene != null)
            return scene;

        Supplier<GuiScene> provider = this.scenes.get(identifier);

        if (provider == null)
            return null;

        try {
            scene = provider.get();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("The scene '" + identifier + "' provider failed.", e);
        }

        if (scene == null)
            throw new IllegalArgumentException("The scene '" + identifier + "' provider returned null.");

        this.instances.put(identifier, scene);
        return scene;

    }


    /**
     * Load a scene from its class.
     * @param identifier The scene identifier, or <code>null</code> to unload current scene.
     * @param oncePreviousStopped An optional consumer launched, if a scene is already loaded,
     *                            after this previous scene {@link GuiScene#stop()} call, the
     *                            consumer parameter is this last scene instance
     */
    public void loadScene(String identifier, Consumer<GuiScene> oncePreviousStopped) {

        GuiScene scene = null;

        if (identifier != null) {

            scene = this.getSceneInstance(identifier);

            if (scene == null || this.currentScene == scene)
                return;

        }

        if (this.currentScene != null) {
            this.currentScene.setDisplayed(false);
            this.currentScene.unloaded();
            if (oncePreviousStopped != null) {
                oncePreviousStopped.accept(this.currentScene);
            }
        }

        this.currentScene = scene;

        if (scene != null) {
            scene.innerInit();
            scene.loaded();
            scene.setDisplayed(true);
        }

    }

}
