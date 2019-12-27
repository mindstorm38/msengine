package io.msengine.client.util.camera;

import io.sutil.math.MathHelper;
import org.joml.Matrix4f;

public class SmoothCamera3D extends Camera3D {

	private float lastX, lastY, lastZ, lastYaw, lastPitch;
	private float targetX, targetY, targetZ, targetYaw, targetPitch;
	private float speed;
	
	public SmoothCamera3D() {
		super();
	}
	
	@Override
	public void reset() {
		
		super.reset();
		
		this.lastX = 0;
		this.lastY = 0;
		this.lastZ = 0;
		this.lastYaw = 0;
		this.lastPitch = 0;
		
		this.targetX = 0;
		this.targetY = 0;
		this.targetZ = 0;
		this.targetYaw = 0;
		this.targetPitch = 0;
		
		this.speed = 1f;
		
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setTarget(float x, float y, float z, float yaw, float pitch) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.targetYaw = yaw;
		this.targetPitch = pitch;
	}
	
	public void addTarget(float dx, float dy, float dz, float dyaw, float dpitch) {
		this.targetX += dx;
		this.targetY += dy;
		this.targetZ += dz;
		this.targetYaw += dyaw;
		this.targetPitch += dpitch;
	}
	
	public void update() {
		
		this.lastX = this.x;
		this.lastY = this.y;
		this.lastZ = this.z;
		this.lastYaw = this.yaw;
		this.lastPitch = this.pitch;
		
		this.x += (this.targetX - this.x) * this.speed;
		this.y += (this.targetY - this.y) * this.speed;
		this.z += (this.targetZ - this.z) * this.speed;
		this.yaw += (this.targetYaw - this.yaw) * this.speed;
		this.pitch += (this.targetPitch - this.pitch) * this.speed;
		
	}
	
	public Matrix4f updateViewMatrix(float alpha) {
		
		this.viewMatrix.identity();
		this.viewMatrix.rotateX(-MathHelper.interpolate(alpha, this.pitch, this.lastPitch));
		this.viewMatrix.rotateY(MathHelper.interpolate(alpha, this.yaw, this.lastYaw));
		this.viewMatrix.translate( -MathHelper.interpolate(alpha, this.x, this.lastX), -MathHelper.interpolate(alpha, this.y, this.lastY), -MathHelper.interpolate(alpha, this.z, this.lastZ) );
		
		return this.viewMatrix;
		
	}
	
}
