package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class Float1Uniform extends Uniform {
	
	private float x;
	
	public void set(float x) {
		if (this.x != x) {
			this.x = x;
			this.setChanged();
		}
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniform1f(this.location, this.x);
	}
	
}
