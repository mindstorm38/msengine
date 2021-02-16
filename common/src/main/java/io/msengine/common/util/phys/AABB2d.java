package io.msengine.common.util.phys;

public class AABB2d {
	
	private double minX, minY;
	private double maxX, maxY;
	
	public AABB2d(double minX, double minY, double maxX, double maxY) {
		this.setPosition(minX, minY, maxX, maxY);
	}
	
	public AABB2d() {
		this.setPositionUnsafe(0, 0, 0, 0);
	}
	
	public AABB2d(AABB2d bb) {
		this.minX = bb.minX;
		this.minY = bb.minY;
		this.maxX = bb.maxX;
		this.maxY = bb.maxY;
	}
	
	public double getMinX() {
		return minX;
	}
	
	public double getMinY() {
		return minY;
	}
	
	public double getMaxX() {
		return maxX;
	}
	
	public double getMaxY() {
		return maxY;
	}
	
	public double getSizeX() {
		return this.maxX - this.minX;
	}
	
	public double getSizeY() {
		return this.maxY - this.minY;
	}
	
	public double getMiddleX() {
		return (this.maxX + this.minX) / 2.0;
	}
	
	public double getMiddleY() {
		return (this.maxY + this.minY) / 2.0;
	}
	
	public void setPosition(double minX, double minY, double maxX, double maxY) {
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
	}
	
	public void setPosition(AABB2d bb) {
		this.setPositionUnsafe(bb.minX, bb.minY, bb.maxX, bb.maxY);
	}
	
	public void setPositionUnsafe(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void move(double dx, double dy) {
		this.minX += dx;
		this.maxX += dx;
		this.minY += dy;
		this.maxY += dy;
	}
	
	public void expand(double x, double y) {
		
		if (x > 0) this.maxX += x;
		else this.minX += x;
		
		if (y > 0) this.maxY += y;
		else this.minY += y;
		
	}
	
	public void grow(double x, double y) {
		
		this.minX -= x;
		this.maxX += x;
		
		this.minY -= y;
		this.maxY += y;
		
	}
	
	public boolean intersects(double x, double y) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
	}
	
	public boolean intersects(double x1, double y1, double x2, double y2) {
		return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1;
	}
	
	public boolean intersects(AABB2d other) {
		return this.intersects(other.minX, other.minY, other.maxX, other.maxY);
	}
	
	public double calcOffsetX(AABB2d other, double offsetX) {
		
		if (other.maxY > this.minY && other.minY < this.maxY) {
			
			if (offsetX > 0.0 && other.maxX <= this.minX) {
				
				double d = this.minX - other.maxX;
				
				if (d < offsetX) {
					offsetX = d;
				}
				
			} else if (offsetX < 0.0 && other.minX >= this.maxX) {
				
				double d = this.maxX - other.minX;
				
				if (d > offsetX) {
					offsetX = d;
				}
				
			}
			
		}
		
		return offsetX;
		
	}
	
	public double calcOffsetY(AABB2d other, double offsetY) {
		
		if (other.maxX > this.minX && other.minX < this.maxX) {
			
			if (offsetY > 0.0 && other.maxY <= this.minY) {
				
				double d = this.minY - other.maxY;
				
				if (d < offsetY) {
					offsetY = d;
				}
				
			} else if (offsetY < 0.0 && other.minY >= this.maxY) {
				
				double d = this.maxY - other.minY;
				
				if (d > offsetY) {
					offsetY = d;
				}
				
			}
			
		}
		
		return offsetY;
		
	}
	
}
