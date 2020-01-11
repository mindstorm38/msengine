package io.msengine.client.renderer.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class ShaderDefaultUniform extends ShaderUniformBase {

	protected ShaderDefaultUniform() {
		super( "default", null );
	}
	
	@Override
	protected void checkUsable() {}

	@Override
	protected void _upload() {}
	
	@Override
	public boolean usable() {
		return true;
	}
	
	@Override
	protected void init() {}
	
	@Override
	protected void delete() {}
	
	@Override
	public void tryUpload() {}
	
	@Override
	protected void upload() {}
	
	@Override
	public void set(int i) {}
	
	@Override
	public void set(int x, int y) {}
	
	@Override
	public void set(int x, int y, int z) {}
	
	@Override
	public void set(int x, int y, int z, int w) {}
	
	@Override
	public void set(Vector2i vec) {}
	
	@Override
	public void set(Vector3i vec) {}
	
	@Override
	public void set(Vector4i vec) {}
	
	@Override
	public void set(float f) {}
	
	@Override
	public void set(float x, float y) {}
	
	@Override
	public void set(float x, float y, float z) {}
	
	@Override
	public void set(float x, float y, float z, float w) {}
	
	@Override
	public void set(Vector2f vec) {}
	
	@Override
	public void set(Vector3f vec) {}
	
	@Override
	public void set(Vector4f vec) {}
	
	@Override
	public void set(Matrix3f mat) {}
	
	@Override
	public void set(Matrix4f mat) {}
	
	@Override
	protected void finalize() throws Throwable {}
	
}
