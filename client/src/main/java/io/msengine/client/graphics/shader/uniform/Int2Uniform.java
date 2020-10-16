package io.msengine.client.graphics.shader.uniform;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;

public class Int2Uniform extends Uniform {
	
	private int x, y;
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}
	
	@Override
	public void upload() {
		GL20.glUniform2f(this.location, this.x, this.y);
	}
	
}
