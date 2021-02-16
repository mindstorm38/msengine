package io.msengine.client.util.camera;

import org.joml.Matrix4f;

/**
 * 
 * A 2D camera used to store camera position, rotation and scale.
 * 
 * @author Théo Rozier (Mindstorm38)
 * @deprecated Because it was too specific, consider creating your own camera in your app.
 *
 */
@Deprecated
public class Camera2D {
	
	public static final Camera2D DEFAULT = new Camera2D();
	
	// Class \\
	
	protected float x, y;
	protected float rotation;
	protected float scale;
	
	protected final Matrix4f viewMatrix;
	
	public Camera2D() {
		
		this.reset();
		
		this.viewMatrix = new Matrix4f();
		this.updateViewMatrix();
		
	}
	
	public void reset() {
		
		this.x = 0;
		this.y = 0;
		this.rotation = 0f;
		this.scale = 1f;
		
	}
	
	// - Position
	
	public float getPosX() {
		return this.x;
	}
	
	public float getPosY() {
		return this.y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void addPosition(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void addPositionX(float x) {
		this.x += x;
	}
	
	public void addPositionY(float y) {
		this.y = y;
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
		this.viewMatrix.translate( -this.x, -this.y, 0 );
		this.viewMatrix.scale( this.scale );
		
		return this.viewMatrix;
		
	}
	
}
