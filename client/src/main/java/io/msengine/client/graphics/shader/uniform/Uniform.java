package io.msengine.client.graphics.shader.uniform;

import io.msengine.client.graphics.shader.ShaderComponent;
import io.msengine.client.graphics.shader.ShaderProgram;

public abstract class Uniform extends ShaderComponent {
	
	protected String identifier;
	protected int location;
	protected boolean changed;
	
	public void setup(ShaderProgram program, String identifier, int location) {
		super.setup(program);
		this.identifier = identifier;
		this.location = location;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public void uploadIfUsed() {
		if (this.isProgramUsed()) {
			this.upload();
			this.changed = false;
		}
	}
	
	public void uploadIfChanged() {
		if (this.changed) {
			this.uploadIfUsed();
		}
	}
	
	protected void setChanged() {
		this.changed = true;
		this.uploadIfUsed();
	}
	
	public abstract void upload();
	
	@Override
	public void close() { }
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<" + this.identifier + "#" + this.location + ">";
	}
	
}
