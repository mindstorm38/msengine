package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class SamplerUniform extends Uniform {
	
	private int activeTexture;
	
	public void setActiveTexture(int activeTexture) {
		this.activeTexture = activeTexture;
	}
	
	@Override
	public void upload() {
		GL20.glUniform1i(this.location, this.activeTexture);
	}
	
}
