package io.msengine.client.audio;

import static org.lwjgl.openal.ALC10.*;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * Audio context for the game
 * 
 * Using OpenAL
 * 
 * @author Mindstorm38
 *
 */
public class AudioContext {
	
	// Singleton \\
	
	private static AudioContext INSTANCE = null;
	
	public static AudioContext getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( AudioContext.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private long device;
	private long context;
	
	private ALCCapabilities alcCapabilities;
	@SuppressWarnings("unused")
	private ALCapabilities alCapabilities;
	
	public AudioContext() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( AudioContext.class );
		INSTANCE = this;
		
		this.device = 0L;
		this.context = 0L;
		
		this.alcCapabilities = null;
		this.alCapabilities = null;
		
	}
	
	/**
	 * Start audio context
	 */
	public void start() {
		
		if ( this.device != 0L ) throw new IllegalStateException("Audio context already started");
		
		LOGGER.info("Starting audio context ...");
		
		String defaultDeviceName = alcGetString( 0, ALC_DEFAULT_DEVICE_SPECIFIER );
		this.device = alcOpenDevice( defaultDeviceName );
		
		int[] attributes = { 0 };
		this.context = alcCreateContext( this.device, attributes );
		alcMakeContextCurrent( this.context );
		
		this.alcCapabilities = ALC.createCapabilities( this.device );
		this.alCapabilities = AL.createCapabilities( this.alcCapabilities );
		
		LOGGER.info("Audio context started");
		
	}
	
	/**
	 * Stop audio context
	 */
	public void stop() {
		
		this.checkAudioContextState();
		
		LOGGER.info("Stoping audio context ...");
		
		alcDestroyContext( this.context );
		alcCloseDevice( this.device );
		
		LOGGER.info("Audio context stoped");
		
	}
	
	/**
	 * Check audio context state, throw exception if no context defined
	 */
	private void checkAudioContextState() {
		if ( this.context == 0L ) throw new IllegalStateException("Audio context not started !");
	}
	
}
