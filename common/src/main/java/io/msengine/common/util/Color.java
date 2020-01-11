package io.msengine.common.util;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.joml.Vector4f;

import io.sutil.LazyLoadValue;

/**
 *
 * Represent a RGBA color.
 *
 * @author Théo Rozier
 *
 */
public class Color {
	
	// Constants \\
	
	public static final Color WHITE = new Color( 1f, 1f, 1f );
	public static final Color BLACK = new Color( 0f, 0f, 0f );
	public static final Color RED = new Color( 1f, 0f, 0f );
	public static final Color GREEN = new Color( 0f, 1f, 0f );
	public static final Color BLUE = new Color( 0f, 0f, 1f );
	
	// Class \\
	
	private float r;
	private float g;
	private float b;
	private float a;
	
	private final LazyLoadValue<Vector3f> vector3;
	private final LazyLoadValue<Vector4f> vector4;
	
	public Color(float r, float g, float b, float a) {
		
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		this.vector3 = new LazyLoadValue<Vector3f>() {
			public Vector3f create() {
				return new Vector3f();
			}
		};
		
		this.vector4 = new LazyLoadValue<Vector4f>() {
			public Vector4f create() {
				return new Vector4f();
			}
		};
		
	}
	
	public Color(float r, float g, float b) {
		this( r, g, b, 1f );
	}
	
	public Color(int r255, int g255, int b255, float a) {
		this( (float) r255 / 255f, (float) g255 / 255f, (float) b255 / 255f, a );
	}
	
	public Color(int r255, int g255, int b255) {
		this( r255, g255, b255, 1f );
	}
	
	public Color() {
		this( 1f, 1f, 1f, 1f );
	}
	
	public void setAll(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setAll(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1f;
	}
	
	public void setAll(int r255, int g255, int b255) {
		this.r = r255 / 255f;
		this.g = g255 / 255f;
		this.b = b255 / 255f;
		this.a = 1f;
	}
	
	public void setAll(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}
	
	public float getRed() { return this.r; }
	public void setRed(float red) { this.r = red; }
	
	public float getGreen() { return this.g; }
	public void setGreen(float green) { this.g = green; }
	
	public float getBlue() { return this.b; }
	public void setBlue(float blue) { this.b = blue; }
	
	public float getAlpha() { return this.a; }
	public void setAlpha(float alpha) { this.a = alpha; }
	
	public void putToBuffer(FloatBuffer buffer, boolean alpha) {
		buffer.put( this.r ).put( this.g ).put( this.b );
		if ( alpha ) buffer.put( this.a );
	}
	
	public void putToBuffer(FloatBuffer buffer) {
		this.putToBuffer( buffer, false );
	}
	
	public Vector4f toVector4f() {
		Vector4f v = this.vector4.get();
		v.set( this.r, this.g, this.b, this.a );
		return v;
	}
	
	public Vector3f toVector3f() {
		Vector3f v = this.vector3.get();
		v.set( this.r, this.g, this.b );
		return v;
	}
	
	public Color copy() {
		return new Color( this.r, this.g, this.b, this.a );
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == this ) return true;
		if ( !( obj instanceof Color ) ) return false;
		Color c = (Color) obj;
		return this.r == c.r && this.g == c.g && this.b == c.b && this.a == c.a;
	}
	
}
