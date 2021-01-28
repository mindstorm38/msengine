package io.msengine.client.renderer.shader;

import io.msengine.client.renderer.vertex.VertexElement;

@Deprecated
public class ShaderAttribute {
	
	private final String identifier;
	private final VertexElement vertexElement;
	private int location; // Linked shader location
	private int index; // Shader manager index
	
	ShaderAttribute(VertexElement vertexElement) {
		
		this.vertexElement = vertexElement;
		this.identifier = vertexElement.getIdentifier();
		this.location = -1;
		this.index = -1;
		
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
	
	public int getIndex() {
		return this.index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	public VertexElement getVertexElement() {
		return this.vertexElement;
	}
	
}
