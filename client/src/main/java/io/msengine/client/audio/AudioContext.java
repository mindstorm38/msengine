package io.msengine.client.audio;

import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.ALC11.*;

/**
 * Audio context using OpenAL.
 * @author Théo Rozier
 */
public class AudioContext implements AutoCloseable {
	
	private long device;
	private long context;
	private ALCCapabilities alcCapabilities;
	private ALCapabilities alCapabilities;
	
	AudioContext(long device, long context, ALCCapabilities alcCapabilities, ALCapabilities alCapabilities) {
		this.device = device;
		this.context = context;
		this.alcCapabilities = alcCapabilities;
		this.alCapabilities = alCapabilities;
	}
	
	public long getDevice() {
		return this.device;
	}
	
	public long getContext() {
		return this.context;
	}
	
	public ALCCapabilities getAlcCapabilities() {
		return this.alcCapabilities;
	}
	
	public ALCapabilities getAlCapabilities() {
		return this.alCapabilities;
	}
	
	public boolean isContextCurrent() {
		return alcGetCurrentContext() == this.context;
	}
	
	@Override
	public void close() {
		
		if (this.context != 0L) {
			alcDestroyContext(this.context);
			this.context = 0L;
		}
		
		if (this.device != 0L) {
			alcCloseDevice(this.device);
			this.device = 0L;
		}
		
	}
	
	/*// Singleton \\
	
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
	/*public void start() {
		
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
	/*public void stop() {
		
		this.checkAudioContextState();
		
		LOGGER.info("Stoping audio context ...");
		
		alcDestroyContext( this.context );
		alcCloseDevice( this.device );
		
		LOGGER.info("Audio context stoped");
		
	}
	
	/**
	 * Check audio context state, throw exception if no context defined
	 */
	/*private void checkAudioContextState() {
		if ( this.context == 0L ) throw new IllegalStateException("Audio context not started !");
	}*/
	
}
