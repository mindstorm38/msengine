package io.msengine.client.util.camera;

import org.joml.Matrix4f;

public class Camera3D {
	
	protected float x, y, z;
	protected float yaw;
	protected float pitch;
	
	private final Matrix4f viewMatrix;
	
	public Camera3D() {
	
		this.viewMatrix = new Matrix4f();
		this.updateViewMatrix();
	
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getZ() {
		return this.z;
	}
	
	public void setPosition(float x, float y, float z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public void addPosition(float x, float y, float z) {
		
		this.x += x;
		this.y += y;
		this.z += z;
		
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void addRotation(float pitch, float yaw) {
		
		this.pitch += pitch;
		this.yaw += yaw;
		
	}
	
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	public Matrix4f updateViewMatrix() {
		
		this.viewMatrix.identity();
		this.viewMatrix.rotateX(-this.pitch);
		this.viewMatrix.rotateY(this.yaw);
		this.viewMatrix.translate( -this.x, -this.y, -this.z );
		
		return this.viewMatrix;
		
	}
	
}
