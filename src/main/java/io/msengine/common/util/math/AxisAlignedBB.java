package io.msengine.common.util.math;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector2f;

import io.msengine.common.osf.OSF;
import io.msengine.common.osf.OSFArray;
import io.msengine.common.osf.OSFNode;
import io.msengine.common.osf.OSFNumber;
import io.msengine.common.osf.serializer.OSFDeserializationContext;
import io.msengine.common.osf.serializer.OSFSerializationContext;
import io.msengine.common.osf.serializer.OSFTypeAdapter;
import io.sutil.LazyLoadValue;

/**
 * 
 * Axis Aligned Bounding Box, used to test collisions
 * 
 * @author Mindstorm38
 *
 */
public class AxisAlignedBB {
	
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
	
	private final LazyLoadValue<List<Vector2f>> vertices;
	
	public AxisAlignedBB(float minX, float minY, float maxX, float maxY) {
		
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		
		this.vertices = new LazyLoadValue<List<Vector2f>>() {
			
			public List<Vector2f> create() {
				return AxisAlignedBB.this.createVertices();
			}
			
		};
		
	}
	
	public AxisAlignedBB() {
		this( 0f, 0f, 0f, 0f );
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
	
	public void move(float x, float y) {
		
		this.minX += x;
		this.maxX += x;
		this.minY += y;
		this.maxY += y;
		
	}
	
	private List<Vector2f> createVertices() {
		return Arrays.asList(
				new Vector2f( this.minX, this.minY ),
				new Vector2f( this.maxX, this.minY ),
				new Vector2f( this.maxX, this.maxY ),
				new Vector2f( this.minX, this.maxY )
		);
	}
	
	public List<Vector2f> getVertices() {
		return this.vertices.get();
	}
	
	public AxisAlignedBB addCoord(float x, float y) {
		
		float minX = this.minX;
		float minY = this.minY;
		float maxX = this.maxX;
		float maxY = this.maxY;
		
		if ( x < 0 ) {
			minX += x;
		} else if ( x > 0 ) {
			maxX += x;
		}
		
		if ( y < 0 ) {
			minY += y;
		} else if ( y > 0 ) {
			maxY += y;
		}
		
		return new AxisAlignedBB( minX, minY, maxX, maxY );
		
	}
	
	public AxisAlignedBB expand(float x, float y) {
		return new AxisAlignedBB( this.minX - x, this.minY - y, this.maxX + x, this.maxY + y );
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
	
	public AxisAlignedBB offset(float x, float y) {
		return new AxisAlignedBB( this.minX + x, this.minY + y, this.maxX + x, this.maxY + y );
	}
	
	public AxisAlignedBB offset(int x, int y) {
		return new AxisAlignedBB( this.minX + x, this.minY + y, this.maxX + x, this.maxY + y );
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
	
	@Override
	public String toString() {
		return "{\"min_x\":" + this.minX + ",\"min_y\":" + this.minY + ",\"max_x\":" + this.maxX + ",\"max_y\":" + this.maxY + "}";
	}
	
}
