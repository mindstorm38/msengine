package io.msengine.client.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.ALC11.*;

public class AudioContextBuilder {
	
	private final Map<Integer, Integer> attributes = new HashMap<>();
	private AudioDevice device = null;

	public AudioContextBuilder withDevice(AudioDevice device) {
		this.device = device;
		return this;
	}
	
	public AudioContextBuilder withDefaultDevice() {
		this.device = null;
		return this;
	}
	
	public AudioContextBuilder withAttribute(int attrib, int val) {
		this.attributes.put(attrib, val);
		return this;
	}
	
	public AudioContextBuilder withFrequency(int frequency) {
		return this.withAttribute(ALC_FREQUENCY, frequency);
	}
	
	public AudioContextBuilder withRefresh(int refresh) {
		return this.withAttribute(ALC_REFRESH, refresh);
	}
	
	public AudioContextBuilder withSync(boolean sync) {
		return this.withAttribute(ALC_SYNC, sync ? ALC_TRUE : ALC_FALSE);
	}
	
	public AudioContextBuilder withMonoSources(int sourcesCount) {
		return this.withAttribute(ALC_MONO_SOURCES, sourcesCount);
	}
	
	public AudioContextBuilder withStereoSources(int sourcesCount) {
		return this.withAttribute(ALC_STEREO_SOURCES, sourcesCount);
	}
	
	public AudioContext build() {
		
		long device = alcOpenDevice(this.device == null ? null : this.device.getName());
		if (device == 0L) {
			throw new IllegalStateException("Failed to open the OpenAL device '" + device + "'.");
		}
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
		long context;
		
		if (this.attributes.isEmpty()) {
			context = alcCreateContext(device, (IntBuffer) null);
		} else {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer attribs = stack.mallocInt((this.attributes.size() << 1) + 1);
				this.attributes.forEach((attrib, val) -> attribs.put(attrib).put(val));
				attribs.put(0);
				context = alcCreateContext(device, attribs);
			}
		}
		
		alcMakeContextCurrent(context);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		return new AudioContext(device, context, alcCapabilities, alCapabilities);
		
	}
	
}
