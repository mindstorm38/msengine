package io.msengine.client.audio;

import io.msengine.client.util.Symbol;

import static org.lwjgl.openal.AL11.*;

public class AudioBufferFormat extends Symbol {
	
	public static final AudioBufferFormat MONO8 = from(AL_FORMAT_MONO8);
	public static final AudioBufferFormat MONO16 = from(AL_FORMAT_MONO16);
	public static final AudioBufferFormat STEREO8 = from(AL_FORMAT_STEREO8);
	public static final AudioBufferFormat STEREO16 = from(AL_FORMAT_STEREO16);
	
	// Class //
	
	public AudioBufferFormat(int value) {
		super(value);
	}
	
	public static AudioBufferFormat from(int value) {
		return new AudioBufferFormat(value);
	}
	
	public static AudioBufferFormat fromChannels(int channels) {
		return channels == 1 ? MONO16 : STEREO16;
	}
	
}
