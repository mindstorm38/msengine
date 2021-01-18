package io.msengine.common.osf;

@Deprecated
public class OSFNumber extends OSFNode {
	
	private Number number;
	
	public OSFNumber(Number number) {
		this.setNumber( number );
	}
	
	public OSFNumber() {
		this( 0 );
	}
	
	public Number getNumber() {
		return this.number;
	}
	
	public void setNumber(Number number) {
		this.number = number == null ? 0 : number;
	}
	
	public boolean isInteger() {
		return this.number instanceof Integer;
	}
	
	public boolean isLong() {
		return this.number instanceof Long;
	}
	
	public boolean isFloat() {
		return this.number instanceof Float;
	}
	
	public boolean isDouble() {
		return this.number instanceof Double;
	}
	
	public boolean isShort() {
		return this.number instanceof Short;
	}
	
	public boolean isByte() {
		return this.number instanceof Byte;
	}
	
	public int getAsInteger() {
		return this.number.intValue();
	}
	
	public long getAsLong() {
		return this.number.longValue();
	}
	
	public float getAsFloat() {
		return this.number.floatValue();
	}
	
	public double getAsDouble() {
		return this.number.doubleValue();
	}
	
	public short getAsShort() {
		return this.number.shortValue();
	}
	
	public byte getAsByte() {
		return this.number.byteValue();
	}
	
	public Class<? extends Number> getNumberClass() {
		return this.number.getClass();
	}
	
	@Override
	public boolean isNumber() {
		return true;
	}
	
	@Override
	public OSFNumber getAsNumber() {
		return this;
	}

	@Override
	public OSFNode copy() {
		return new OSFNumber( this.number );
	}
	
	@Override
	public String toString() {
		return this.number.toString();
	}
	
}
