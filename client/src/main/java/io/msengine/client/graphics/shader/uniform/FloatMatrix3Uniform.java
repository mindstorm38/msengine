package io.msengine.client.graphics.shader.uniform;

import org.joml.Matrix3f;
import org.lwjgl.opengl.GL20;

public class FloatMatrix3Uniform extends FloatBufferUniform {
	
	@Override
	protected int getBufferSize() {
		return 9;
	}
	
	public void set(Matrix3f mat) {
		mat.get(0, this.ensureBuffer());
		this.setChanged();
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniformMatrix3fv(this.location, false, this.buffer);
	}
	
}
