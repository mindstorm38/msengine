package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class Float1Uniform extends Uniform {
	
	private float x;
	
	public void set(float x) {
		this.x = x;
		this.setChanged();
	}
	
	@Override
	public void upload() {
		GL20.glUniform1f(this.location, this.x);
	}
	
}
