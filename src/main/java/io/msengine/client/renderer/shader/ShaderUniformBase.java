package io.msengine.client.renderer.shader;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryUtil;

import io.msengine.common.util.Color;
import io.sutil.ReflectUtils;

public abstract class ShaderUniformBase {

	protected final String identifier;
	protected final ShaderValueType type;
	protected int location;
	
	protected Buffer buffer;
	protected IntBuffer intBuffer;
	protected FloatBuffer floatBuffer;
	
	public ShaderUniformBase(String identifier, ShaderValueType type) {
		
		this.identifier = identifier;
		this.type = type;
		this.location = -1;
		
		this.reset();
		
	}
	
	protected void checkUsable() {
		if ( this.buffer == null ) throw new IllegalStateException("This uniform is not usable, you must initialize the shader manager first. If persisting, check if uniform successfuly found in shader program.");
	}
	
	public boolean usable() {
		return this.buffer != null;
	}
	
	protected void init() {
		
		this.buffer = this.type.createBufferFunction.apply( this.type.size );
		this.intBuffer = ReflectUtils.safecast( this.buffer, IntBuffer.class );
		this.floatBuffer = ReflectUtils.safecast( this.buffer, FloatBuffer.class );
		
	}
	
	protected void delete() {
		
		this.checkUsable();
		
		MemoryUtil.memFree( this.buffer );
		
		this.reset();
		
	}
	
	private void reset() {
		
		this.buffer = null;
		this.intBuffer = null;
		this.floatBuffer = null;
		
	}
	
	protected abstract void _upload();
	
	protected void upload() {
		
		this.checkUsable();
		this._upload();
		
	}
	
	public abstract void tryUpload();
	
	public void set(boolean b) {
		this.set( b ? 1 : 0 );
	}
	
	public void set(int i) {
		
		this.checkUsable();
		this.intBuffer.put( 0, i );
		this.tryUpload();
		
	}
	
	public void set(int x, int y) {

		this.checkUsable();
		this.intBuffer.put( 0, x );
		this.intBuffer.put( 1, y );
		this.tryUpload();
		
	}
	
	public void set(int x, int y, int z) {

		this.checkUsable();
		this.intBuffer.put( 0, x );
		this.intBuffer.put( 1, y );
		this.intBuffer.put( 2, z );
		this.tryUpload();
		
	}
	
	public void set(int x, int y, int z, int w) {

		this.checkUsable();
		this.intBuffer.put( 0, x );
		this.intBuffer.put( 1, y );
		this.intBuffer.put( 2, z );
		this.intBuffer.put( 3, z );
		this.tryUpload();
		
	}
	
	public void set(Vector2i vec) {

		this.checkUsable();
		vec.get( 0, this.intBuffer );
		this.tryUpload();
		
	}
	
	public void set(Vector3i vec) {

		this.checkUsable();
		vec.get( 0, this.intBuffer );
		this.tryUpload();
		
	}
	
	public void set(Vector4i vec) {

		this.checkUsable();
		vec.get( 0, this.intBuffer );
		this.tryUpload();
		
	}
	
	public void set(float f) {

		this.checkUsable();
		this.floatBuffer.put( 0, f );
		this.tryUpload();
		
	}
	
	public void set(float x, float y) {

		this.checkUsable();
		this.floatBuffer.put( 0, x );
		this.floatBuffer.put( 1, y );
		this.tryUpload();
		
	}
	
	public void set(float x, float y, float z) {

		this.checkUsable();
		this.floatBuffer.put( 0, x );
		this.floatBuffer.put( 1, y );
		this.floatBuffer.put( 2, z );
		this.tryUpload();
		
	}
	
	public void set(float x, float y, float z, float w) {

		this.checkUsable();
		this.floatBuffer.put( 0, x );
		this.floatBuffer.put( 1, y );
		this.floatBuffer.put( 2, z );
		this.floatBuffer.put( 3, w );
		this.tryUpload();
		
	}
	
	public void set(Vector2f vec) {

		this.checkUsable();
		vec.get( 0, this.floatBuffer );
		this.tryUpload();
		
	}
	
	public void set(Vector3f vec) {

		this.checkUsable();
		vec.get( 0, this.floatBuffer );
		this.tryUpload();
		
	}
	
	public void set(Vector4f vec) {

		this.checkUsable();
		vec.get( 0, this.floatBuffer );
		this.tryUpload();
		
	}
	
	public void set(Matrix3f mat) {

		this.checkUsable();
		mat.get( 0, this.floatBuffer );
		this.tryUpload();
		
	}
	
	public void set(Matrix4f mat) {

		this.checkUsable();
		mat.get( 0, this.floatBuffer );
		this.tryUpload();
		
	}
	
	// Sub setters
	
	public void setRGB(Color color) {
		this.set( color.getRed(), color.getGreen(), color.getBlue() );
	}
	
	public void setRGBA(Color color) {
		this.set( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( this.buffer != null ) this.delete();
	}
	
}
