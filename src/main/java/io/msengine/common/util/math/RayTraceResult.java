package io.msengine.common.util.math;

public class RayTraceResult {
	
	protected final float newToX, newToY;
	
	public RayTraceResult(float newToX, float newToY) {
		
		this.newToX = newToX;
		this.newToY = newToY;
		
	}
	
	public float getNewToX() {
		return this.newToX;
	}
	
	public float getNewToY() {
		return this.newToY;
	}
	
}
