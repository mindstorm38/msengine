package io.msengine.client.renderer.shader;

public class ShaderSampler {

	// Class \\
	
	protected final String identifier;
	protected int location;
	protected ShaderSamplerObject object;
	
	ShaderSampler(String identifier) {
		
		this.identifier = identifier;
		this.location = -1;
		this.object = null;
		
	}
	
}
