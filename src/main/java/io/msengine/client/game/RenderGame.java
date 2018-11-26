package io.msengine.client.game;

import java.io.File;

import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.util.RenderConstantFields;
import io.msengine.client.renderer.window.Window;
import io.msengine.common.game.ServerGame;
import io.sutil.CommonUtils;
import io.sutil.ThreadUtils;

public abstract class RenderGame<E extends RenderGameOptions> extends ServerGame<E> {

	public static final float DEFAULT_FPS = 60f;
	
	protected float fpsInterval;
	protected float delta;
	protected float accumulator;
	protected float alpha;
	protected double time;
	protected boolean sync;

	protected final Window window;
	protected final TextureManager textureManager;
	protected final RenderConstantFields renderConstantFields;
	
	protected final File screeshots;
	
	protected RenderGame(E options) {
		
		super( options );
		
		this.setFPS( DEFAULT_FPS );
		
		this.window = new Window();
		this.textureManager = new TextureManager();
		this.renderConstantFields = new RenderConstantFields();
		
		this.screeshots = null;
		
	}
	
	public void setFPS(float fps) {
		this.fpsInterval = 1f / ( fps == 0f ? DEFAULT_FPS : fps );
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
		
		this.window.start( this.bootoptions.getInitialWindowTitle() );
		
		this.renderConstantFields.init();
		
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
