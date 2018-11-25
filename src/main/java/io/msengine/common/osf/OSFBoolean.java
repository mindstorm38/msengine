package io.msengine.common.osf;

public class OSFBoolean extends OSFNode {
	
	private boolean bool;
	
	public OSFBoolean(boolean bool) {
		this.setBoolean( bool );
	}
	
	public boolean getBoolean() {
		return this.bool;
	}
	
	public void setBoolean(boolean bool) {
		this.bool = bool;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public OSFBoolean getAsBoolean() {
		return this;
	}

	@Override
	public OSFNode copy() {
		return new OSFBoolean( this.bool );
	}
	
	@Override
	public String toString() {
		return this.bool ? "true" : "false";
	}
	
}
