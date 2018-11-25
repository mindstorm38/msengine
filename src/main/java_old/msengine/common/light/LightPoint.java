package msengine.common.light;

import msengine.client.util.Color;
import msengine.common.util.AxisAlignedBB;

public class LightPoint {
	
	private float posX;
	private float posY;
	
	private float power;
	
	private Color color;
	
	private final AxisAlignedBB boundingBox;
	
	public LightPoint(float x, float y) {
		
		this.posX = x;
		this.posY = y;
		
		this.power = 1.0f;
		
		this.color = Color.WHITE.copy();
		this.boundingBox = new AxisAlignedBB();
		
		this.calculateBoundingBox();
		
	}
	
	public void calculateBoundingBox() {
		this.boundingBox.setPositions( this.posX - this.power, this.posY - this.power, this.posX + this.power, this.posY + this.power );
	}
	
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}
	
	public float getX() { return this.posX; }
	public float getY() { return this.posY; }
	
	public void setPosition(float x, float y) {
		
		this.posX = x;
		this.posY = y;
		
	}
	
	public float getPower() { return this.power; }
	
	public void setPower(float power) {
		if ( this.power < 0.0f ) throw new IllegalArgumentException("Invalid power value, must be greater or equals to 0");
		this.power = power;
	}
	
	public Color getColor() { return this.color; }
	
}
