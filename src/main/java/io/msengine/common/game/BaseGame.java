package io.msengine.common.game;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.msengine.client.option.Options;
import io.msengine.common.resource.I18n;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.util.GameLogger;

import static io.msengine.common.util.GameLogger.LOGGER;

public abstract class BaseGame<O extends BaseGameOptions> {

	// Static \\
	
	private static BaseGame<?> CURRENT = null;
	
	// Class \\
	
	protected final O bootoptions;
	
	protected final ResourceManager resourceManager;
	protected final Options options;
	protected final Logger logger;
	
	protected final File appdata;
	
	protected boolean running;
	protected boolean error;
	
	protected BaseGame(O bootoptions) {
		
		if ( CURRENT != null )
			throw new IllegalStateException("An instance of BaseGame already instancied, one game at a time !");
		
		CURRENT = this;
		
		this.bootoptions = bootoptions;
		
		this.resourceManager = new ResourceManager( bootoptions.getRunningClass(), bootoptions.getResourceBaseFolderPath() );
		this.options = new Options( bootoptions.getOptionsFile() );
		this.logger = GameLogger.create( bootoptions.getLoggerName() );
		
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
	
	public File getAppdata() {
		return this.appdata;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	protected void init() {
		
		try {
			I18n.getInstance().init();
		} catch (Exception e) {
			LOGGER.log( Level.WARNING, e.getMessage(), e );
		}
		
	}
	
	protected abstract void setuploop();
	protected abstract void loop();
	protected abstract void stop();

	public void start() {
	
		if ( this.running ) throw new IllegalStateException("Game already running !");
		this.running = true;
		
		this.error = false;
		
		try {
			
			this.logger.info("Starting the game ...");
			
			this.init();
			
			this.setuploop();
			
			while ( this.running ) {
				
				this.loop();
				
			}
			
			this.logger.info("Game started");
			
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
