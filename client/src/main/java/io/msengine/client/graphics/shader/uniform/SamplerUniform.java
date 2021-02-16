package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class SamplerUniform extends Uniform {
	
	private int unit;
	
	public void setTextureUnit(int unit) {
		if (this.unit != unit) {
			this.unit = unit;
			this.setChanged();
		}
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniform1i(this.location, this.unit);
	}
	
}
