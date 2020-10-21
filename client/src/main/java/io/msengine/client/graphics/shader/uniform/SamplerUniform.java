package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class SamplerUniform extends Uniform {
	
	private int unit;
	
	public void setTextureUnit(int unit) {
		this.unit = unit;
	}
	
	@Override
	public void upload() {
		GL20.glUniform1i(this.location, this.unit);
	}
	
}
