package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.mask.GuiMask;
import io.msengine.client.graphics.gui.render.GuiProgramType;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.gui.render.GuiStdProgram;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.graphics.util.Blending;
import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.window.ContextWindow;
import io.msengine.client.window.Window;
import io.msengine.client.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class GuiManager implements WindowFramebufferSizeEventListener, ModelApplyListener {
	
	private static final Logger LOGGER = Logger.getLogger("mse.gui");
	
	private final ContextWindow window;
	private final Map<String, Supplier<GuiScene>> scenes = new HashMap<>();
	private final Map<String, GuiScene> instances = new HashMap<>();
	
	private final ModelHandler model = new ModelHandler(this);
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final Color globalColor = new Color();
	
	private final Map<GuiProgramType<?>, ProgramTracker> programs = new HashMap<>();
	private final List<GuiStdProgram> stdPrograms = new ArrayList<>();
	private GuiProgramType<?> currentProgramType;
	private ShaderProgram currentProgram;
	
	//private GuiProgramMain program;
	private GuiScene currentScene;
	
	private boolean rendering;
	private boolean masking;
	
	public GuiManager(ContextWindow window) {
		this.window = Objects.requireNonNull(window, "Missing window.");
	}
	
	public ContextWindow getWindow() {
		return this.window;
	}
	
	protected GuiProgramMain createProgram() {
		return new GuiProgramMain();
	}
	
	/**
	 * Internal method to check if the used window's context is the current one.
	 * <b>This method is not called for {@link #render(float)} to avoid overhead,
	 * but render should be called in the valid context.</b>
	 */
	private void checkWindowContext() {
		if (!this.window.isContextCurrent()) {
			throw new IllegalStateException("Can't call this because The GUI manager window's context is not the current one.");
		}
	}
	
	/**
	 * Init the manager, this require a bound OpenGL context
	 * and a valid window.
	 * @throws IllegalStateException If the manager window's context is not the current one.
	 */
	public void init() {
		/*if (this.program == null) {
			this.checkWindowContext();
			this.program = this.createProgram();
			this.program.link();
			this.updateSceneSizeFromWindow();
			this.window.getEventManager().addEventListener(WindowFramebufferSizeEventListener.class, this);
		}*/
		this.checkWindowContext();
		this.updateSceneSizeFromWindow();
		this.window.getEventManager().addEventListener(WindowFramebufferSizeEventListener.class, this);
	}
	
	/**
	 * Stop the manager.
	 */
	public void stop() {
		//if (this.program != null) {
			this.checkWindowContext();
			this.window.getEventManager().removeEventListener(WindowFramebufferSizeEventListener.class, this);
			this.unloadScene();
			this.instances.values().forEach(GuiScene::stop);
			this.instances.clear();
			//this.program.close();
			//this.program = null;
		//}
	}
	
	public void render(float alpha) {
		
		if (this.rendering) {
			throw new IllegalStateException("Already rendering GUI.");
		}
		
		if (this.currentScene != null/* && this.program != null*/) {
			
			this.rendering = true;
			this.masking = false;
			
			Blending.enable();
			Blending.transparency();
			
			/*this.program.use();
			this.program.setTextureUnit(null);
			this.program.setGlobalColor(Color.WHITE);
			this.program.uploadProjectionMatrix();*/
			
			this.currentScene.render(alpha);
			
			if (this.currentProgram != null) {
				ShaderProgram.release();
				this.currentProgramType = null;
				this.currentProgram = null;
			}
			
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
		
		this.checkWindowContext();
		
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
		this.checkWindowContext();
		GuiScene scene = this.instances.get(identifier);
		if (scene != this.currentScene) {
			this.instances.remove(identifier);
			if (scene != null) {
				scene.innerStop();
			}
		}
	}
	
	// Dynamic shader program usage //
	
	/**
	 * Internal structure to track uses of a shader program type.
	 */
	private static class ProgramTracker {
		private final ShaderProgram program;
		private int uses = 1;
		private ProgramTracker(ShaderProgram program) {
			this.program = program;
		}
	}
	
	/**
	 * <p>Acquire a shader program for future uses in render loop. Calling this method
	 * will link the created program if it's the first time it is allocated.</p>
	 * <p>This method can be called everywhere before a call to {@link #useProgram(GuiProgramType)},
	 * but it is advisable to call it in {@link GuiObject#init()} since this method can take
	 * too much time for render loop.</p>
	 * <p>If the supplied {@link ShaderProgram} implements {@link GuiStdProgram}, this program will
	 * be updated when the model or the projection matrix are updated by the manager.</p>
	 * @param type The program type to acquire.
	 * @param <P> A subclass of {@link ShaderProgram} supplied by this type.
	 * @return The acquired {@link ShaderProgram}.
	 */
	@SuppressWarnings("unchecked")
	public <P extends ShaderProgram> P acquireProgram(GuiProgramType<P> type) {
		
		ProgramTracker tracker = this.programs.get(type);
		
		if (tracker == null) {
			
			tracker = new ProgramTracker(type.supplyProgram());
			tracker.program.link();
			this.programs.put(type, tracker);
			
			if (tracker.program instanceof GuiStdProgram) {
				GuiStdProgram stdProgram = (GuiStdProgram) tracker.program;
				stdProgram.setProjectionMatrix(this.projectionMatrix);
				stdProgram.setModelMatrix(this.model.current());
				this.stdPrograms.add(stdProgram);
			}
			
		} else {
			tracker.uses++;
		}
		
		return (P) tracker.program;
		
	}
	
	/**
	 * Release a shader program, after this call, the caller must no longer use
	 * this program in the render loop. It's advisable to call this in {@link GuiObject#stop()}.
	 * @param type The shader program type to release.
	 */
	public void releaseProgram(GuiProgramType<?> type) {
		ProgramTracker tracker = this.programs.get(type);
		if (tracker != null) {
			if (--tracker.uses <= 0) {
				tracker.program.close();
				this.programs.remove(type);
				if (tracker.program instanceof GuiStdProgram) {
					this.stdPrograms.remove(tracker.program);
				}
			}
		}
	}
	
	/**
	 * Get a shader program previously acquired, if not acquired the result is
	 * undefined (null).
	 * @param type The program type to acquire.
	 * @param <P> A subclass of {@link ShaderProgram} supplied by this type.
	 * @return The acquired shader program, or null if released.
	 */
	@SuppressWarnings("unchecked")
	public <P extends ShaderProgram> P getProgram(GuiProgramType<P> type) {
		ProgramTracker tracker = this.programs.get(type);
		return tracker == null ? null : (P) tracker.program;
	}
	
	/**
	 * Ensure that a shader program is currently used for the rendering pipeline,
	 * the caller must have previously acquired the program using
	 * {@link #acquireProgram(GuiProgramType)} and must call
	 * {@link #releaseProgram(GuiProgramType)} when the program is no
	 * longer needed for future render cycles.
	 * @param type The shader program type.
	 * @param <P> A subclass of {@link ShaderProgram} supplied by this type.
	 * @return The used shader program instance.
	 */
	@SuppressWarnings("unchecked")
	public <P extends ShaderProgram> P useProgram(GuiProgramType<P> type) {
		GuiProgramType<?> current = this.currentProgramType;
		if (current == type) {
			return (P) this.currentProgram;
		}
		ProgramTracker tracker = this.programs.get(type);
		if (tracker == null) {
			throw new IllegalArgumentException("Invalid program type '" + type.getName() + "', must be acquired first.");
		}
		if (current != null) {
			ShaderProgram.release();
		}
		ShaderProgram program = tracker.program;
		program.use();
		if (program instanceof GuiStdProgram) {
			((GuiStdProgram) program).uploadProjectionMatrix();
		}
		this.currentProgramType = type;
		this.currentProgram = program;
		return (P) program;
	}
	
	// Rendering variables //
	
	/*
	 * @return The shader program used when rendering.
	 */
	/*public GuiProgramMain getProgram() {
		return this.program;
	}*/
	
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
	/*public void setGlobalColor(Color color) {
		this.program.setGlobalColor(color);
	}
	
	public void setTextureUnit(Integer unit) {
		this.program.setTextureUnit(unit);
	}
	
	public void setTextureUnitAndBind(int unit, int name) {
		this.setTextureUnit(unit);
		Texture.bindTexture(unit, GL_TEXTURE_2D, name);
	}
	
	public void resetTextureUnitAndUnbind() {
		this.setTextureUnit(null);
		Texture.unbindTexture(GL_TEXTURE_2D);
	}*/
	
	public void mask(GuiMask[] masks) {
		
		if (!this.rendering) {
			throw new IllegalStateException("Masking is only available");
		}
		
		if (this.masking) {
			throw new IllegalStateException("Already masking");
		}
		
		this.masking = true;
		// this.program.setTextureUnit(null);
		
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
		this.stdPrograms.forEach(p -> p.setProjectionMatrix(this.projectionMatrix));
		// this.program.setProjectionMatrix(this.projectionMatrix);
		
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
		this.stdPrograms.forEach(p -> p.setModelMatrix(model));
		// this.program.setModelMatrix(model);
	}
	
}
