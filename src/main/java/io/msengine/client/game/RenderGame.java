package io.msengine.client.game;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.util.RenderConstantFields;
import io.msengine.client.renderer.window.Window;
import io.msengine.common.game.ServerGame;
import io.msengine.common.resource.I18n;
import io.sutil.CommonUtils;
import io.sutil.ThreadUtils;
import io.sutil.lang.LanguageManager;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VENDOR;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static io.msengine.common.util.GameLogger.LOGGER;

public abstract class RenderGame<E extends RenderGameOptions> extends ServerGame<E> {

	public static final float DEFAULT_FPS = 60f;
	
	protected float fpsInterval;
	protected float delta;
	protected float accumulator;
	protected float alpha;
	protected double time;
	protected boolean sync;

	protected final LanguageManager languageManager;
	
	protected final Window window;
	protected final TextureManager textureManager;
	protected final RenderConstantFields renderConstantFields;
	
	protected final File screeshots;
	
	protected RenderGame(E options) {
		
		super( options );
		
		this.setFPS( DEFAULT_FPS );
		
		this.languageManager = new I18n( bootoptions.getBaseLangsFolderPath() );
		
		this.window = new Window();
		this.textureManager = new TextureManager();
		this.renderConstantFields = new RenderConstantFields();
		
		this.screeshots = null;
		
	}
	
	public void setFPS(float fps) {
		this.fpsInterval = 1f / ( fps == 0f ? DEFAULT_FPS : fps );
	}
	
	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}
	
	public Window getWindow() {
		return this.window;
	}
	
	public TextureManager getTextureManager() {
		return this.textureManager;
	}
	
	public RenderConstantFields getRenderConstantFields() {
		return this.renderConstantFields;
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		try {
			this.options.load();
		} catch (IOException e) {
			LOGGER.log( Level.WARNING, "Enable to load options, using default !", e );
		}
		
		this.window.start( this.bootoptions.getInitialWindowTitle() );
		
		this.renderConstantFields.init();
		
		LOGGER.info("Context constants :");
		LOGGER.info( "- LWJGL : " + org.lwjgl.Version.getVersion() );
		LOGGER.info( "- GLFW : " + glfwGetVersionString() );
		LOGGER.info( "- OpenGL : " + glGetString( GL_VERSION ) + " (" + glGetString( GL_VENDOR ) + ")" );
		LOGGER.info( "- Renderer : " + glGetString( GL_RENDERER ) );
		LOGGER.info( "- GLSL : " + glGetString( GL_SHADING_LANGUAGE_VERSION ) );
		LOGGER.info( "- OpenAL : " + alGetString( AL_VERSION ) + " (" + alGetString( AL_VENDOR ) + ")" );
		
	}
	
	@Override
	protected void setuploop() {
		
		super.setuploop();
		
		this.delta = 0f;
		this.accumulator = 0f;
		this.alpha = 0f;
		this.time = CommonUtils.getTime();
		
		this.sync = true;
		
	}

	@Override
	protected void loop() {
		
		if ( this.window.shouldClose() ) this.running = false;
		
		this.now = CommonUtils.getTime();
		this.delta = (float) ( this.now - this.time );
		this.accumulator += this.delta;
		
		while ( this.accumulator >= this.tpsInterval ) {
			
			this.tick();
			this.accumulator -= this.tpsInterval;
			
		}
		
		this.alpha = this.accumulator / this.tpsInterval;
		
		this.winrender( this.alpha );
		
		this.time = this.now;
		
		if ( this.sync ) this.sync( this.time );
		
	}
	
	private void sync(double lastLoopTime) {
		
		while ( CommonUtils.getTime() - lastLoopTime < this.fpsInterval )
			ThreadUtils.safesleep( 1L );
		
	}

	@Override
	protected void stop() {
		
		super.stop();
		
	}
	
	protected void winrender(float alpha) {
		
		this.window.pollEvents();
		
		this.render( alpha );
		
		this.window.swapBuffers();
		
	}
	
	protected abstract void render(float alpha);

}
