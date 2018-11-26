package io.msengine.client.util;

import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * 
 * @author Mindstorm38
 *
 */
public class Camera2D {
	
	public static final Camera2D DEFAULT = new Camera2D();
	
	// Class \\
	
	private final Vector2f position;
	private float rotation;
	private float scale;
	
	private final Matrix4f viewMatrix;
	
	public Camera2D() {
		
		this.position = new Vector2f();
		
		this.reset();
		
		this.viewMatrix = new Matrix4f();
		this.updateViewMatrix();
		
	}
	
	public void reset() {
		
		this.position.zero();
		this.rotation = 0f;
		this.scale = 1f;
		
	}
	
	// - Position
	
	public Vector2f getPosition() {
		return this.position;
	}
	
	public float getPosX() {
		return this.position.x;
	}
	
	public float getPosY() {
		return this.position.y;
	}
	
	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}
	
	public void addPosition(float x, float y) {
		this.position.x += x;
		this.position.y += y;
	}
	
	public void addPositionX(float x) {
		this.position.x += x;
	}
	
	public void addPositionY(float y) {
		this.position.y = y;
	}
	
	// - Rotation
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public void addRotation(float rotation) {
		this.rotation += rotation;
	}
	
	// - Scale
	
	public float getScale() {
		return this.scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void addScale(float scale) {
		this.scale += scale;
	}
	
	// - View matrix
	
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	public Matrix4f updateViewMatrix() {
		
		this.viewMatrix.identity();
		this.viewMatrix.rotateY( this.rotation );
		this.viewMatrix.translate( -this.position.x, -this.position.y, 0 );
		this.viewMatrix.scale( this.scale );
		
		return this.viewMatrix;
		
	}
	
}
