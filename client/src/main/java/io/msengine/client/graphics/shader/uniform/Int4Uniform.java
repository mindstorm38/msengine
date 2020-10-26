package io.msengine.client.graphics.shader.uniform;

import org.joml.Vector4i;
import org.lwjgl.opengl.GL20;

public class Int4Uniform extends IntBufferUniform {
	
	@Override
	protected int getBufferSize() {
		return 4;
	}
	
	public void set(int x, int y, int z, int w) {
		this.ensureBuffer();
		this.buffer.put(0, x);
		this.buffer.put(1, y);
		this.buffer.put(2, z);
		this.buffer.put(3, w);
		this.setChanged();
	}
	
	public void set(Vector4i vec) {
		vec.get(0, this.ensureBuffer());
		this.setChanged();
	}
	
	@Override
	public void upload() {
		GL20.glUniform4iv(this.location, this.buffer);
	}
	
}
