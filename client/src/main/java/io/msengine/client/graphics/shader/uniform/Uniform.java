package io.msengine.client.graphics.shader.uniform;

public abstract class Uniform implements AutoCloseable {
	
	protected String identifier;
	protected int location;
	
	public void setup(String identifier, int location) {
		this.identifier = identifier;
		this.location = location;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public abstract void upload();
	
	@Override
	public void close() { }
	
}
