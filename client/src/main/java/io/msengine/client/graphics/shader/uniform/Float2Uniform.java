package io.msengine.client.graphics.shader.uniform;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class Float2Uniform extends Uniform {
	
	private float x, y;
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
		this.uploadIfUsed();
	}
	
	public void set(Vector2f vec) {
		this.set(vec.x, vec.y);
	}
	
	@Override
	public void upload() {
		GL20.glUniform2f(this.location, this.x, this.y);
	}
	
}
