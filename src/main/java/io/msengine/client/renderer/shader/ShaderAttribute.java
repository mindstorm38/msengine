package io.msengine.client.renderer.shader;

import io.msengine.client.renderer.vertex.VertexElement;

public class ShaderAttribute {
	
	protected final String identifier;
	protected final VertexElement vertexElement;
	protected int location;
	
	ShaderAttribute(VertexElement vertexElement) {
		
		this.vertexElement = vertexElement;
		this.identifier = vertexElement.getIdentifier();
		this.location = -1;
		
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public VertexElement getVertexElement() {
		return this.vertexElement;
	}
	
}
