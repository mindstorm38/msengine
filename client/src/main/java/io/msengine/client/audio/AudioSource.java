package io.msengine.client.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL11.*;

public class AudioSource implements AutoCloseable {
	
	private int name;
	
	public AudioSource() {
		this.name = alGenSources();
		if (!alIsSource(this.name)) {
			throw new IllegalStateException("Failed to create the audio source.");
		}
	}
	
	public boolean isValid() {
		return alIsSource(this.name);
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The audio source is no longer valid.");
		}
	}
	
	public void setBuffer(int bufferName) {
		alSourcei(this.name, AL_BUFFER, bufferName);
	}
	
	public void setBuffer(AudioBuffer buffer) {
		this.setBuffer(buffer.getName());
	}
	
	public void setRelativePosition(boolean relative) {
		alSourcei(this.name, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
	}
	
	public void setPosition(float x, float y, float z) {
		alSource3f(this.name, AL_POSITION, x, y, z);
	}
	
	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}
	
	public void setVelocity(float dx, float dy, float dz) {
		alSource3f(this.name, AL_VELOCITY, dx, dy, dz);
	}
	
	public void setVelocity(Vector3f vel) {
		this.setVelocity(vel.x, vel.y, vel.z);
	}
	
	public void setGain(float gain) {
		alSourcef(this.name, AL_GAIN, gain);
	}
	
	public void setPitch(float pitch) {
		alSourcef(this.name, AL_PITCH, pitch);
	}
	
	public void setLooping(boolean looping) {
		alSourcei(this.name, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
	}
	
	public void play() {
		alSourcePlay(this.name);
	}
	
	public void pause() {
		alSourcePause(this.name);
	}
	
	public void stop() {
		alSourceStop(this.name);
	}
	
	public boolean isPlaying() {
		return alGetSourcei(this.name, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	@Override
	public void close() {
		if (alIsSource(this.name)) {
			alDeleteSources(this.name);
			this.name = 0;
		}
	}
	
}
