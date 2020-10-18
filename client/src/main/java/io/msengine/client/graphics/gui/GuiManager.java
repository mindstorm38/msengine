package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.mask.GuiMask;
import io.msengine.client.graphics.gui.render.GuiShaderProgram;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.client.window.Window;
import io.msengine.client.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class GuiManager implements WindowFramebufferSizeEventListener, ModelApplyListener {

    private static final Logger LOGGER = Logger.getLogger("msengine.gui");

    private final Window window;
    private final Map<String, Supplier<GuiScene>> scenes = new HashMap<>();
    private final Map<String, GuiScene> instances = new HashMap<>();
    
    private final ModelHandler model = new ModelHandler(this);
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Color globalColor = new Color();
    
    private GuiShaderProgram program;
    private GuiScene currentScene;
    
    private boolean rendering;
    private boolean masking;
    
    public GuiManager(Window window) {
        this.window = Objects.requireNonNull(window, "Missing window.");
    }

    public Window getWindow() {
        return this.window;
    }

    protected GuiShaderProgram createProgram() {
        return new GuiShaderProgram();
    }
    
    public void init() {
        if (this.program  == null) {
            this.program = this.createProgram();
            this.program.link();
            this.updateSceneSizeFromWindow();
            this.window.getEventManager().addEventListener(WindowFramebufferSizeEventListener.class, this);
        }
    }

    public void stop() {
        if (this.program != null) {
            this.window.getEventManager().removeEventListener(WindowFramebufferSizeEventListener.class, this);
            this.unloadScene();
            this.instances.values().forEach(GuiScene::stop);
            this.instances.clear();
            this.program.close();
            this.program = null;
        }
    }
    
    public void render(float alpha) {
        
        if (this.rendering) {
            throw new IllegalStateException("Already rendering GUI.");
        }
        
        if (this.currentScene != null && this.program != null) {
    
            this.rendering = true;
            this.masking = false;
            
            glEnable(GL_BLEND);
            BlendMode.TRANSPARENCY.use();
            this.program.use();
            this.setGlobalColor(Color.WHITE);
            
            this.currentScene.render(alpha);
            
            ShaderProgram.release();
            
            this.unmask();
            this.rendering = false;
            
        }
        
    }
    
    public void update() {
        if (this.currentScene != null) {
            this.currentScene.update();
        }
    }
    
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
            
            scene.innerInit(this);
            scene.loaded();
            
            // Not calling updateSceneSizeFromWindow() because we
            // don't need to update the renderer.
            this.window.getFramebufferSize(scene::setSceneSize);
            
            scene.setDisplayed(true);
            
        }

    }

    /**
     * Load a scene from its registered identifier.
     * @param identifier The scene identifier.
     * @see #loadScene(String, Consumer)
     */
    public void loadScene(String identifier) {
        this.loadScene(identifier, null);
    }

    /**
     * Unload the current loaded class
     */
    public void unloadScene() {
        this.loadScene(null, null);
    }

    /**
     * Force uncaching of a cached scene instance, this call its stop() method.
     * This can't be the current loaded scene.
     * @param identifier The scene identifier.
     */
    public void uncacheScene(String identifier) {
        GuiScene scene = this.instances.get(identifier);
        if (scene != this.currentScene) {
            this.instances.remove(identifier);
            if (scene != null) {
                scene.innerStop();
            }
        }
    }
    
    // Rendering variables //
    
    /**
     * @return The shader program used when rendering.
     */
    public GuiShaderProgram getProgram() {
        return this.program;
    }
    
    /**
     * @return The model (for model matrix) used when rendering.
     */
    public ModelHandler getModel() {
        return this.model;
    }
    
    /**
     * @return The mutable global color.
     */
    public Color getGlobalColor() {
        return this.globalColor;
    }
    
    /**
     * Upload the global color to the program uniform.
     */
    public void applyGlobalColor() {
        this.program.setGlobalColor(this.globalColor);
    }
    
    public void setGlobalColor(Color color) {
        this.globalColor.setAll(color);
        this.applyGlobalColor();
    }
    
    public void mask(GuiMask[] masks) {
        
        if (!this.rendering) {
            throw new IllegalStateException("Masking is only available");
        }
        
        if (this.masking) {
            throw new IllegalStateException("Already masking");
        }
        
        this.masking = true;
        this.program.setTextureEnabled(false);
        
        glEnable(GL_STENCIL_TEST);
        
        glStencilMask(1);
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glColorMask(false, false, false, false);
        
        for (GuiMask mask : masks) {
            mask.draw();
        }
        
        glStencilMask(1);
        glStencilFunc(GL_EQUAL, 1, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glColorMask(true, true, true, true);
        
    }
    
    public void unmask() {
        if (this.rendering && this.masking) {
            glDisable(GL_STENCIL_TEST);
            this.masking = false;
        }
    }
    
    // Scene Size //
    
    private void updateSceneSize(int width, int height) {
        
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0, width, height, 0, 1024, -1024);
        this.program.setProjectionMatrix(this.projectionMatrix);
        
        if (this.currentScene != null) {
            this.currentScene.setSceneSize(width, height);
        }
        
    }
    
    private void updateSceneSizeFromWindow() {
        this.window.getFramebufferSize(this::updateSceneSize);
    }
    
    @Override
    public void onWindowFramebufferSizeChangedEvent(Window origin, int width, int height) {
        if (origin == this.window) {
            this.updateSceneSize(width, height);
        }
    }
    
    @Override
    public void modelApply(Matrix4f model) {
        this.program.setModelMatrix(model);
    }
    
}
