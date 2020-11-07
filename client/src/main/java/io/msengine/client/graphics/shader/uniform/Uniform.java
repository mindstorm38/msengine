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
	
	/**
	 * No longer use, too slow.
	 */
	@Deprecated
	public void uploadIfUsed() {
		if (this.isProgramUsed()) {
			this.innerUpload();
			this.changed = false;
		}
	}
	
	public void uploadIfChanged() {
		if (this.changed) {
			this.upload();
			// this.uploadIfUsed();
		}
	}
	
	public void upload() {
		this.innerUpload();
		this.changed = false;
	}
	
	public void setChanged() {
		this.changed = true;
		// this.uploadIfUsed();
	}
	
	protected abstract void innerUpload();
	
	@Override
	public void close() { }
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<" + this.identifier + "#" + this.location + ">";
	}
	
}
