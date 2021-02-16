package io.msengine.client.audio;

import io.msengine.client.util.Symbol;

import static org.lwjgl.openal.AL11.*;

public class AudioDistanceModel extends Symbol {
	
	public static final AudioDistanceModel INVERSE_DISTANCE = from(AL_INVERSE_DISTANCE);
	public static final AudioDistanceModel INVERSE_DISTANCE_CLAMPED = from(AL_INVERSE_DISTANCE_CLAMPED);
	
	public static final AudioDistanceModel LINEAR_DISTANCE = from(AL_LINEAR_DISTANCE);
	public static final AudioDistanceModel LINEAR_DISTANCE_CLAMPED = from(AL_LINEAR_DISTANCE_CLAMPED);
	
	public static final AudioDistanceModel EXPONENT_DISTANCE = from(AL_EXPONENT_DISTANCE);
	public static final AudioDistanceModel EXPONENT_DISTANCE_CLAMPED = from(AL_EXPONENT_DISTANCE_CLAMPED);
	
	public static final AudioDistanceModel NONE = from(AL_NONE);
	
	// Class //
	
	public AudioDistanceModel(int value) {
		super(value);
	}
	
	private static AudioDistanceModel from(int value) {
		return new AudioDistanceModel(value);
	}
	
}
