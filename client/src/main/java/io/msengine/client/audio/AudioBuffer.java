package io.msengine.client.audio;

import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

public class AudioBuffer implements AutoCloseable {

	private int name;

	public AudioBuffer() {
		this.name = alGenBuffers();
		if (!alIsBuffer(this.name)) {
			throw new IllegalStateException("Failed to create the audio buffer.");
		}
	}
	
	public int getName() {
		return this.name;
	}
	
	public boolean isValid() {
		return alIsBuffer(this.name);
	}
	
	public void checkValid() {
		if (!this.isValid()) {
			throw new IllegalStateException("The audio buffer is no longer valid.");
		}
	}
	
	public void uploadData(AudioBufferFormat format, ShortBuffer data, int frequency) {
		this.checkValid();
		alBufferData(this.name, format.value, data, frequency);
	}
	
	public void uploadVorbis(ByteBuffer data) throws IOException {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer channels = stack.mallocInt(1);
			IntBuffer sampleRate = stack.mallocInt(1);
			ShortBuffer buffer = stb_vorbis_decode_memory(data, channels, sampleRate);
			if (buffer == null) {
				throw new IOException("Failed to decode OGG vorbis data.");
			}
			this.uploadData(AudioBufferFormat.fromChannels(channels.get(0)), buffer, sampleRate.get(0));
			LibCStdlib.free(buffer);
		}
	}
	
	public void uploadVorbisFromStream(InputStream stream) throws IOException {
		this.uploadVorbis(BufferAlloc.fromInputStream(stream));
	}
	
	public void uploadVorbisFromAsset(Asset asset) throws IOException {
		this.uploadVorbisFromStream(asset.openStreamExcept());
	}
	
	@Override
	public void close() {
		if (alIsBuffer(this.name)) {
			alDeleteBuffers(this.name);
			this.name = 0;
		}
	}
	
}
