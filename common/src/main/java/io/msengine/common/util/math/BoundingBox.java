package io.msengine.common.util.math;

import io.sutil.math.MathHelper;

@Deprecated
public class BoundingBox implements RectBoundingBox {

	public static final float PI = (float) Math.PI;
	public static final float PI_HALF = (float) MathHelper.PI_HALF;
	
	private float posX;
	private float posY;
	private float width;
	private float height;
	private float angle;
	private float vangle;
	
	public BoundingBox(float posX, float posY, float width, float height, float angle) {

		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.vangle = angle + PI_HALF;
		
	}
	
	public BoundingBox(float posX, float posY, float width, float height) {
		this( posX, posY, width, height, 0f );
	}

	@Override
	public float[] getCorners() {
		
		final float[] pos = new float[ 8 ];
		
		float tanAngle = (float) Math.atan2( this.height, this.width ) + this.angle;
		float tanLen = (float) Math.sqrt( this.width * this.width + this.height * this.height );
		
		pos[0] = this.posX;
		pos[1] = this.posY;
		
		pos[2] = this.posX + (float) Math.cos( this.angle ) * this.width;
		pos[3] = this.posY + (float) Math.sin( this.angle ) * this.width;
		
		pos[4] = this.posX + (float) Math.cos( tanAngle ) * tanLen;
		pos[5] = this.posY + (float) Math.sin( tanAngle ) * tanLen;
		
		pos[6] = this.posX + (float) Math.cos( this.vangle ) * this.height;
		pos[7] = this.posY + (float) Math.sin( this.vangle ) * this.height;
		
		return pos;
		
	}
	
	public void moveAbs(float x, float y) {
		
		this.posX += x;
		this.posY += y;
		
	}
	
	public void moveRel(float x, float y) {
		
		float offX = (float) Math.cos( this.angle ) * x + (float) Math.cos( this.vangle ) * y;
		float offY = (float) Math.sin( this.angle ) * x + (float) Math.sin( this.vangle ) * y;
		
		this.moveAbs( offX, offY );
		
	}
	
	public void rotateRel(float angle, float originX, float originY) {
		
		float posAngle = (float) Math.atan2( originX, originY ) + this.angle;
		float posAngleOpos = posAngle + PI + angle;
		float posDist = (float) Math.sqrt( originX * originX + originY * originX );
		
		this.posX += (float) Math.cos( posAngle ) * posDist + (float) Math.cos( posAngleOpos ) * posDist;
		this.posY += (float) Math.sin( posAngle ) * posDist + (float) Math.sin( posAngleOpos ) * posDist;
		
		this.angle += angle;
		this.vangle = this.angle + PI_HALF;
		
	}
	
	public void addSize(float x, float y) {
		
		if ( x < 0 ) {
			
			this.width -= x;
			
			this.posX += (float) Math.cos( this.angle ) * x;
			this.posY += (float) Math.sin( this.angle ) * x;
			
		} else {
			this.width += x;
		}
		
		if ( y < 0 ) {
			
			this.height -= y;
			
			this.posX += (float) Math.cos( this.vangle ) * y;
			this.posY += (float) Math.sin( this.vangle ) * y;
			
		} else {
			this.height += y;
		}
		
	}
	
	/*
	public boolean intersects(float x, float y) {
		
		float ptAngle = (float) Math.atan2( y, x ) - this.angle;
		
		
	}
	*/
	
	/*
	public void intersects(BoundingBox box) {
		
		
		
	}
	*/
	
}
