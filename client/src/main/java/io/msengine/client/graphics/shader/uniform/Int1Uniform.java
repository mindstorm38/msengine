package io.msengine.client.graphics.shader.uniform;

import org.lwjgl.opengl.GL20;

public class Int1Uniform extends Uniform {
	
	private int x;
	
	public void set(int x) {
		if (this.x != x) {
			this.x = x;
			this.setChanged();
		}
	}
	
	public void set(boolean b) {
		this.set(b ? 1 : 0);
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniform1i(this.location, this.x);
	}
	
}
