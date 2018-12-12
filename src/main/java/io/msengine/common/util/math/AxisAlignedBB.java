package io.msengine.common.util.math;

import io.msengine.common.osf.OSF;
import io.msengine.common.osf.OSFArray;
import io.msengine.common.osf.OSFNode;
import io.msengine.common.osf.OSFNumber;
import io.msengine.common.osf.serializer.OSFDeserializationContext;
import io.msengine.common.osf.serializer.OSFSerializationContext;
import io.msengine.common.osf.serializer.OSFTypeAdapter;

/**
 * 
 * Axis Aligned Bounding Box, used to test collisions
 * 
 * @author Mindstorm38
 *
 */
public class AxisAlignedBB implements RectBoundingBox {
	
	// Constants \\
	
	public static final AxisAlignedBB ZERO = new AxisAlignedBB( 0, 0, 0, 0 );
	public static final AxisAlignedBB FULL = new AxisAlignedBB( 0, 0, 1, 1 );
	
	// Static \\
	
	public static class TypeAdapter implements OSFTypeAdapter<AxisAlignedBB> {

		public static final TypeAdapter INSTANCE = new TypeAdapter();
		
		private TypeAdapter() {}
		
		@Override
		public Class<AxisAlignedBB> initOSF(OSF osf) {
			return AxisAlignedBB.class;
		}
		
		@Override
		public OSFNode serialize(AxisAlignedBB obj, OSFSerializationContext context) {
			
			OSFArray arr = new OSFArray();
			arr.add( new OSFNumber( obj.minX ) );
			arr.add( new OSFNumber( obj.minY ) );
			arr.add( new OSFNumber( obj.maxX ) );
			arr.add( new OSFNumber( obj.maxY ) );
			return arr;
			
		}

		@Override
		public AxisAlignedBB deserialize(OSFNode node, OSFDeserializationContext context) {
			
			OSFArray arr = node.getAsArray();
			if ( arr == null ) return null;
			
			try {
				
				float minX = arr.get( 0 ).getAsNumber().getAsFloat();
				float minY = arr.get( 1 ).getAsNumber().getAsFloat();
				float maxX = arr.get( 2 ).getAsNumber().getAsFloat();
				float maxY = arr.get( 3 ).getAsNumber().getAsFloat();
				
				if ( minX == maxX || minY == maxY ) return null;
				
				return new AxisAlignedBB( minX, minY, maxX, maxY );
			
			} catch (Exception e) {
				return null;
			}
			
		}

	}
	
	// Class \\
	
	private float minX;
	private float minY;
	
	private float maxX;
	private float maxY;
	
	public AxisAlignedBB(float minX, float minY, float maxX, float maxY) {
		
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		
	}
	
	public AxisAlignedBB() {
		this( 0f, 0f, 0f, 0f );
	}

	@Override
	public float[] getCorners() {
		
		final float[] corners = new float[8];
		
		corners[0] = this.minX;
		corners[1] = this.minY;
		
		corners[2] = this.maxX;
		corners[3] = this.minY;
		
		corners[4] = this.maxX;
		corners[5] = this.maxY;
		
		corners[6] = this.minX;
		corners[7] = this.maxY;
		
		return corners;
		
	}
	
	public float getMinX() {
		return this.minX;
	}
	
	public void setMinX(float minX, boolean move) {
		if ( move ) this.maxX += minX - this.minX;
		this.minX = minX;
	}
	
	public void setMinX(float minX) {
		this.setMinX( minX, false );
	}
	
	public float getMinY() {
		return this.minY;
	}
	
	public void setMinY(float minY, boolean move) {
		if ( move ) this.maxY += minY - this.minY;
		this.minY = minY;
	}
	
	public void setMinY(float minY) {
		this.setMinY( minY, false );
	}

	public float getMaxX() {
		return this.maxX;
	}
	
	public void setMaxX(float maxX, boolean move) {
		if ( move ) this.minX += maxX - this.maxX;
		this.maxX = maxX;
	}

	public void setMaxX(float maxX) {
		this.setMaxX( maxX, false );
	}
	
	public float getMaxY() {
		return this.maxY;
	}
	
	public void setMaxY(float maxY, boolean move) {
		if ( move ) this.minY += maxY - this.maxY;
		this.maxY = maxY;
	}
	
	public void setMaxY(float maxY) {
		this.setMaxY( maxY, false );
	}
	
	public void setPositions(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public float getMiddleX() {
		return ( this.minX + this.maxX ) / 2f;
	}
	
	public float getMiddleY() {
		return ( this.minY + this.maxY ) / 2f;
	}
	
	public AxisAlignedBB move(float x, float y) {
		
		this.minX += x;
		this.maxX += x;
		this.minY += y;
		this.maxY += y;
		
		return this;
		
	}
	
	public AxisAlignedBB addCoord(float x, float y) {
		
		if ( x < 0 ) {
			this.minX += x;
		} else {
			this.maxX += x;
		}
		
		if ( y < 0 ) {
			this.minY += y;
		} else {
			this.maxY += y;
		}
		
		return this;
		
	}
	
	public AxisAlignedBB expand(float x, float y) {
		
		this.minX -= x;
		this.minY -= y;
		this.maxX += x;
		this.maxY += y;
		
		return this;
		
	}
	
	public AxisAlignedBB expand(float xy) {
		return this.expand( xy, xy );
	}
	
	public AxisAlignedBB contract(float x, float y) {
		return this.expand( -x, -y );
	}
	
	public AxisAlignedBB contract(float xy) {
		return this.expand( -xy );
	}
	
	/**
	 * Test if a point intersects with this bounding box
	 * @param x Point X position
	 * @param y Point Y position
	 * @return Point intersects or not
	 */
	public boolean intersects(float x, float y) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
	}
	
	public boolean intersects(float minX, float minY, float maxX, float maxY) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY;
	}
	
	public boolean intersects(AxisAlignedBB other) {
		return this.intersects( other.minX, other.minY, other.maxX, other.maxY );
	}
	
	public float calculateXOffset(AxisAlignedBB other, float offsetX) {
		
		/*
		if ( other.maxY > this.minY && other.minY < this.maxY ) {
			
			if ( offsetX > 0 && other.maxX > this.minX ) {
				System.out.println( "offsetX:" + offsetX );
				float f = offsetX - other.maxX - this.minX;
				System.out.println( "f:" + f );
				if ( f < offsetX ) {
					offsetX = f;
				}
				
			} else if ( offsetX < 0 && other.minX < this.maxX ) {
				
				float f = offsetX - other.minX - this.maxX;
				
				if ( f > offsetX ) {
					offsetX = f;
				}
				
			}
			
		}
		
		return offsetX;
		*/
		
		if ( other.maxY > this.minY && other.minY < this.maxY ) {
			
			if ( offsetX > 0 && other.maxX <= this.minX ) {
				
				float f = this.minX - other.maxX;
				
				if ( f < offsetX ) {
					offsetX = f;
				}
				
			} else if ( offsetX < 0 && other.minX >= this.maxX ) {
				
				float f = this.maxX - other.minX;
				
				if ( f > offsetX ) {
					offsetX = f;
				}
				
			}
			
			return offsetX;
			
		} else {
			
			return offsetX;
			
		}
		
	}
	
	public float calculateYOffset(AxisAlignedBB other, float offsetY) {
		
		/*
		if ( other.maxX > this.minX && other.minX < this.maxX ) {
			
			if ( offsetY > 0 && other.maxY > this.minY ) {
				
				float f = offsetY - other.maxY - this.minY;
				
				if ( f < offsetY ) {
					offsetY = f;
				}
				
			} else if ( offsetY < 0 && other.minY < this.maxY ) {
				
				float f = offsetY - other.minX - this.maxX;
				
				if ( f > offsetY ) {
					offsetY = f;
				}
				
			}
			
		}
			
		return offsetY;
		*/
		
		if ( other.maxX > this.minX && other.minX < this.maxX ) {
			
			if ( offsetY > 0 && other.maxY <= this.minY ) {
				
				float f = this.minY - other.maxY;
				
				if ( f < offsetY ) {
					offsetY = f;
				}
				
			} else if ( offsetY < 0 && other.minY >= this.maxY ) {
				
				float f = this.maxY - other.minY;
				
				if ( f > offsetY ) {
					offsetY = f;
				}
				
			}
			
			return offsetY;
			
		} else {
			
			return offsetY;
			
		}
		
	}
	
	public AxisAlignedBB copy() {
		return new AxisAlignedBB( this.minX, this.minY, this.maxX, this.maxY );
	}
	
	public AxisAlignedBB copyAddCoord(float x, float y) {
		return this.copy().addCoord( x, y );
	}
	
	public AxisAlignedBB copyMove(float x, float y) {
		return new AxisAlignedBB( this.minX + x, this.minY + y, this.maxX + x, this.maxY + y );
	}
	
	public RayTraceResult intersectsRayLine(Ray ray) {
		
		float a = ray.getA();
		float b = ray.getB();
		
		float xBottom = ( this.minY - b ) / a;
		float xTop = ( this.maxY - b ) / a;
		float yLeft = a * this.minX + b;
		float yRight = a * this.maxX + b;
		
		if ( ray.getVecX() > 0f ) {
			
			if ( yLeft >= this.minY && yLeft <= this.maxY ) 
				return new RayTraceResult( this.minX, yLeft );
			
		} else if ( ray.getVecX() < 0f ) {
			
			if ( yRight >= this.minY && yRight <= this.maxY )
				return new RayTraceResult( this.maxX, yRight );
			
		}
		
		if ( ray.getVecY() > 0f ) {
			
			if ( xBottom >= this.minX && xBottom <= this.maxX )
				return new RayTraceResult( xBottom, this.minY );
			
		} else if ( ray.getVecY() < 0f ) {
			
			if ( xTop >= this.minX && xTop <= this.maxX )
				return new RayTraceResult( xTop, this.maxY );
			
		}
		
		return null;
		
	}
	
	public RayTraceResult intersectsRay(Ray ray) {
		
		RayTraceResult res = this.intersectsRayLine( ray );
		if ( res == null ) return null;
		
		//if ( res.getNewToX() > ray.getToX() || res.getNewToX() < ray.getToX() ) return null;
		//if ( res.getNewToY() > ray.getToY() || res.getNewToX() < ray.getFromY() ) return null;
		
		return res;
		
	}
	
	@Override
	public String toString() {
		return "{\"min_x\":" + this.minX + ",\"min_y\":" + this.minY + ",\"max_x\":" + this.maxX + ",\"max_y\":" + this.maxY + "}";
	}
	
}
