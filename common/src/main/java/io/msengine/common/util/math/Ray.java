package io.msengine.common.util.math;

public class Ray {

	private float fromX, fromY;
	private float toX, toY;
	
	private float vecX, vecY;
	
	private float a, b;
	
	public Ray(float fromX, float fromY, float toX, float toY) {
		
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		
		this.vecX = toX - fromX;
		this.vecY = toY - fromY;
		
		this.a = this.vecY / this.vecX;
		this.b = fromY - ( this.a * fromX );
		
	}
	
	public float getFromX() {
		return this.fromX;
	}
	
	public float getFromY() {
		return this.fromY;
	}
	
	public float getToX() {
		return this.toX;
	}
	
	public float getToY() {
		return this.toY;
	}
	
	public float getVecX() {
		return this.vecX;
	}
	
	public float getVecY() {
		return this.vecY;
	}
	
	public float getA() {
		return this.a;
	}
	
	public float getB() {
		return this.b;
	}
	
	public float calcY(float x) {
		return this.a * x + this.b;
	}
	
}
