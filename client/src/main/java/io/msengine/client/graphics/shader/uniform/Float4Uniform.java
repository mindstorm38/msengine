package io.msengine.client.graphics.shader.uniform;

import io.msengine.common.util.Color;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class Float4Uniform extends FloatBufferUniform {
	
	@Override
	protected int getBufferSize() {
		return 4;
	}
	
	public void set(float x, float y, float z, float w) {
		this.ensureBuffer();
		this.buffer.put(0, x);
		this.buffer.put(1, y);
		this.buffer.put(2, z);
		this.buffer.put(3, w);
		this.setChanged();
	}
	
	public void set(Vector4f vec) {
		vec.get(0, this.ensureBuffer());
		this.setChanged();
	}
	
	public void set(Color color) {
		this.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	@Override
	public void upload() {
		GL20.glUniform4fv(this.location, this.buffer);
	}
	
}
