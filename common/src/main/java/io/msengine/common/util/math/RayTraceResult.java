package io.msengine.common.util.math;

@Deprecated
public class RayTraceResult {
	
	private final float newToX, newToY;
	
	public RayTraceResult(float newToX, float newToY) {
		
		this.newToX = newToX;
		this.newToY = newToY;
		
	}
	
	public RayTraceResult(RayTraceResult origin) {
		this( origin.newToX, origin.newToY );
	}
	
	public float getNewToX() {
		return this.newToX;
	}
	
	public float getNewToY() {
		return this.newToY;
	}
	
	@Override
	public String toString() {
		return "{RayTraceResult: " + this.newToX + "/" + this.newToY + "}";
	}
	
}
