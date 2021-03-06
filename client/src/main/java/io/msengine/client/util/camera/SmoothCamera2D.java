package io.msengine.client.util.camera;

import org.joml.Matrix4f;

import io.sutil.math.MathHelper;

/**
 * 
 * A {@link Camera2D} with smooth effect using {@link #setSpeed(float)}, {@link #update()} and {@link #updateViewMatrix(float)}.
 * 
 * @author Mindstorm38
 * @deprecated Because it was too specific, consider creating your own camera in your app.
 *
 */
@Deprecated
public class SmoothCamera2D extends Camera2D {

	private float lastX, lastY;
	private float targetX, targetY;
	private float speed;
	
	public SmoothCamera2D() {
		
		super();
		
	}
	
	@Override
	public void reset() {
		
		super.reset();
		
		this.lastX = 0;
		this.lastY = 0;
		
		this.targetX = 0;
		this.targetY = 0;
		
		this.speed = 1f;
		
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setTarget(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public void addTarget(float targetX, float targetY) {
		this.targetX += targetX;
		this.targetY += targetY;
	}
	
	public void update() {
		
		this.lastX = this.x;
		this.lastY = this.y;
		
		this.x += ( this.targetX - this.x ) * this.speed;
		this.y += ( this.targetY - this.y ) * this.speed;
		
	}
	
	public Matrix4f updateViewMatrix(float alpha) {
		
		this.viewMatrix.identity();
		this.viewMatrix.rotateY( this.rotation );
		this.viewMatrix.translate( -MathHelper.interpolate( alpha, this.x, this.lastX ), -MathHelper.interpolate( alpha, this.y, this.lastY ), 0 );
		this.viewMatrix.scale( this.scale );
		
		return this.viewMatrix;
		
	}
	
}
