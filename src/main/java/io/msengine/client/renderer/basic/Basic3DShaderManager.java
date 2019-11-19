package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.vertex.VertexElement;

public class Basic3DShaderManager extends BasicShaderManager {
	
	public Basic3DShaderManager(String identifier) {
		super(identifier, "basic3d", VertexElement.POSITION_3F);
	}
	
}
