package io.msengine.client.audio;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL11.*;

public class AudioListener {
	
	public static void setPosition(float x, float y, float z) {
		alListener3f(AL_POSITION, x, y, z);
	}
	
	public static void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	public static void setVelocity(float x, float y, float z) {
		alListener3f(AL_VELOCITY, x, y, z);
	}
	
	public static void setVelocity(Vector3f vel) {
		setVelocity(vel.x, vel.y, vel.z);
	}
	
	public static void setOrientation(Vector3f at, Vector3f up) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(6);
			buf.put(at.x).put(at.y).put(at.z);
			buf.put(up.x).put(up.y).put(up.z);
			buf.flip();
			alListenerfv(AL_ORIENTATION, buf);
		}
	}
	
	public static void setGain(float gain) {
		alListenerf(AL_GAIN, gain);
	}
	
}
