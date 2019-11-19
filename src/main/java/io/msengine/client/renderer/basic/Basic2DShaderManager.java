package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.vertex.VertexElement;

public class Basic2DShaderManager extends BasicShaderManager {
	
	public Basic2DShaderManager(String identifier) {
		super(identifier, "basic2d", VertexElement.POSITION_2F);
	}
}
