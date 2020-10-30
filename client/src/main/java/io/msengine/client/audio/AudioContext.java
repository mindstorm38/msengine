package io.msengine.client.audio;

import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.AL11.*;

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
	
	/**
	 * Suspends processing on this context, all successive call to "<b>al*</b>" are
	 * queued and will be processed when processing ({@link #process()}) this context
	 * again.
	 */
	public void suspend() {
		if (this.context != 0L) {
			alcSuspendContext(this.context);
		}
	}
	
	/**
	 * Process a context, if previously suspended ({@link #suspend()}), all queued calls
	 * to "<b>al*</b>" are applied at once.
	 */
	public void process() {
		if (this.context != 0L) {
			alcSuspendContext(this.context);
		}
	}
	
	/**
	 * @return True if this context is the current one for the current thread (or
	 * process for special contexts).
	 */
	public boolean isContextCurrent() {
		return alcGetCurrentContext() == this.context;
	}
	
	/**
	 * @throws IllegalStateException If this context is not the current one for the
	 * current thread (or process for special contexts).
	 */
	public void checkContextCurrent() {
		if (!this.isContextCurrent()) {
			throw new IllegalStateException("The audio context must be the current one.");
		}
	}
	
	/**
	 * Set the distance model used to attenuate the sound.
	 * @param model Distance model (non null).
	 * @apiNote https://www.openal.org/documentation/OpenAL_Programmers_Guide.pdf [p.87]
	 */
	public static void setDistanceModel(AudioDistanceModel model) {
		alDistanceModel(model.value);
	}
	
	/**
	 * Scaling of source and listener velocities to exaggerateor deemphasize
	 * the Doppler (pitch) shift resulting from the calculation.
	 * @param factor Doppler factor (> 0).
	 * @apiNote https://www.openal.org/documentation/OpenAL_Programmers_Guide.pdf [p.14-15]
	 */
	public static void setDopplerFactor(float factor) {
		alDopplerFactor(factor);
	}
	
	/**
	 * <p>Set the speed of sound for current context, source and listener velocities
	 * should be expressed in the same units as the speed of sound.</p>
	 * <p>Distance and velocity units are completely independent of one another
	 * (so you could use different units for each if desired). If an OpenAL
	 * application doesn't want to use Doppler effects, then leaving all velocities
	 * at zero will achieve that result.</p>
	 * @param speed Speed of sound (> 0).
	 * @apiNote https://www.openal.org/documentation/OpenAL_Programmers_Guide.pdf [p.15]
	 */
	public static void setSpeedOfSound(float speed) {
		alSpeedOfSound(speed);
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
