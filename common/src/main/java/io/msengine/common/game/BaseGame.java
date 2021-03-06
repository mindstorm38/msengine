package io.msengine.common.game;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.msengine.common.resource.I18n;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.util.GameLogger;
import io.msengine.common.util.GameProfiler;
import io.msengine.common.util.GameTypeRequired;
import io.sutil.profiler.Profiler;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * Base class for all games.
 *
 * @param <SELF>You must set this to the extended class.
 * @param <O> The class used for game options.
 */
@Deprecated
public abstract class BaseGame<SELF extends BaseGame<SELF, O>, O extends BaseGameOptions> {

	// Static \\
	
	protected static BaseGame<?, ?> CURRENT = null;
	
	public static BaseGame<?, ?> getCurrent() {
		if ( CURRENT == null ) throw new GameTypeRequired( BaseGame.class );
		return CURRENT;
	}
	
	// Class \\
	
	protected final O bootoptions;
	
	protected final ResourceManager resourceManager;
	protected final Logger logger;
	
	protected final Profiler profiler;
	
	protected final File appdata;
	
	protected boolean running;
	protected boolean error;
	
	protected BaseGame(O bootoptions) {
		
		if ( CURRENT != null )
			throw new IllegalStateException("An instance of BaseGame already instancied, one game at a time !");
		
		CURRENT = this;
		
		this.bootoptions = bootoptions;
		
		this.resourceManager = new ResourceManager(bootoptions.getRunningClass(), bootoptions.getResourceBaseFolderPath(), bootoptions.getResourceNamespace());
		this.logger = GameLogger.create(bootoptions.getLoggerName());
		
		this.profiler = GameProfiler.getInstance();
		
		this.appdata = bootoptions.getAppdataDir();
		
		this.running = false;
		this.error = false;
		
	}
	
	public O getBootOptions() {
		return this.bootoptions;
	}
	
	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public Logger getLogger() {
		return this.logger;
	}
	
	public Profiler getProfiler() {
		return this.profiler;
	}
	
	public File getAppdata() {
		return this.appdata;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	protected void init() {
		
		if (!this.appdata.isDirectory() && !this.appdata.mkdirs())
			throw new RuntimeException("Failed to make appdata directory.");
		
		try {
			I18n.getInstance().init();
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
		}
		
	}
	
	protected void setuploop() {}
	
	protected abstract void loop();
	
	protected void stop() {}

	public void start() {
	
		if ( this.running ) throw new IllegalStateException("Game already running !");
		this.running = true;
		
		this.error = false;
		
		try {
			
			this.logger.info("Starting the game ...");
			
			this.init();
			
			this.setuploop();
			
			this.logger.info("Game started");
			
			while ( this.running ) {
				
				this.profiler.startSection("root");
				
				this.loop();
				
				this.profiler.endSection();
				
			}
			
		} catch ( Throwable e ) {
			
			this.logger.log( Level.SEVERE, null, e );
			
			this.error = true;
			
		} finally {
			
			this.logger.info("Stoping game ...");
			
			this.stop();
			
			this.logger.info("Game stoped");
			
			System.exit( this.error ? 1 : 0 );
			
		}
		
	}
	
	/**
	 * Stop the game
	 */
	public void stopRunning() {
		this.running = false;
	}

}
