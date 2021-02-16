package io.msengine.client.renderer.shader;

import io.msengine.client.renderer.texture.TextureObject;

@Deprecated
public class ShaderSampler {

	// Class \\
	
	private final String identifier;
	private int location;
	private int activeTexture;
	private ShaderSamplerObject object;
	
	ShaderSampler(String identifier) {
		
		this.identifier = identifier;
		this.location = -1;
		this.object = null;
		
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	void setLocation(int location) {
		this.location = location;
	}
	
	public int getActiveTexture() {
		return this.activeTexture;
	}
	
	void setActiveTexture(int activeTexture) {
		this.activeTexture = activeTexture;
	}
	
	public ShaderSamplerObject getSamplerObject() {
		return this.object;
	}
	
	public boolean hasSamplerObject() {
		return this.object != null;
	}
	
	public void setSamplerObject(ShaderSamplerObject object) {
		this.object = object;
	}
	
	public void bind() {
		
		if (this.object == null) {
			TextureObject.unbind(this.activeTexture);
		} else {
			TextureObject.bindTexture(this.object.getSamplerId(), this.activeTexture);
		}
		
	}
	
}
