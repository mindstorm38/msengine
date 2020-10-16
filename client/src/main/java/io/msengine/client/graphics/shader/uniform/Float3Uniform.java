package io.msengine.client.graphics.shader.uniform;

import io.msengine.common.util.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class Float3Uniform extends Uniform {
	
	private float x, y, z;
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
	
	public void set(Color color) {
		this.x = color.getRed();
		this.y = color.getGreen();
		this.z = color.getBlue();
	}
	
	@Override
	public void upload() {
		GL20.glUniform3f(this.location, this.x, this.y, this.z);
	}
	
}
