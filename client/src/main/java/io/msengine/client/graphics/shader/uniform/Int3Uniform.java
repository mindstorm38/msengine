package io.msengine.client.graphics.shader.uniform;

import org.joml.Vector3i;
import org.lwjgl.opengl.GL20;

public class Int3Uniform extends Uniform {
	
	private int x, y, z;
	
	public void set(int x, int y, int z) {
		if (this.x != x || this.y != y || this.z != z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.setChanged();
		}
	}
	
	public void set(Vector3i vec) {
		this.set(vec.x, vec.y, vec.z);
	}
	
	@Override
	protected void innerUpload() {
		GL20.glUniform3f(this.location, this.x, this.y, this.z);
	}
	
}
