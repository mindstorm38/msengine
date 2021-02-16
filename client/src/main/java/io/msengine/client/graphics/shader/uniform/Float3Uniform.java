package io.msengine.client.graphics.shader.uniform;

import io.msengine.common.util.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class Float3Uniform extends Uniform {
	
	private float x, y, z;
	
	public void set(float x, float y, float z) {
		if (this.x != x || this.y != y || this.z != z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.setChanged();
		}
	}
	
	public void set(Vector3f vec) {
		this.set(vec.x, vec.y, vec.z);
	}
	
	public void set(Color color) {
		this.set(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniform3f(this.location, this.x, this.y, this.z);
	}
	
}
