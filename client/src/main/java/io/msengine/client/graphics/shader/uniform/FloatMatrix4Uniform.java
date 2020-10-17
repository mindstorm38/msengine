package io.msengine.client.graphics.shader.uniform;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

public class FloatMatrix4Uniform extends FloatBufferUniform {
	
	@Override
	protected int getBufferSize() {
		return 16;
	}
	
	public void set(Matrix4f mat) {
		mat.get(0, this.ensureBuffer());
		this.uploadIfUsed();
	}
	
	@Override
	public void upload() {
		GL20.glUniformMatrix4fv(this.location, false, this.buffer);
	}
	
}
