package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.VertexElement;
import io.sutil.CollectionUtils;

import static io.msengine.client.renderer.vertex.type.BasicFormat.*;

public class Basic3DShaderManager extends BasicShaderManager {
	
	private static final String[] VERTEX_ATTRIBS = {BASIC3D_POSITION, BASIC_COLOR, BASIC_TEX_COORD};
	
	public Basic3DShaderManager(String identifier) {
		super(identifier, "basic3d", VertexElement.POSITION_3F);
	}
	
	public Basic3DShaderManager(String identifier, String fragmentShaderIdentifier) {
		super(identifier, "basic3d", fragmentShaderIdentifier, VertexElement.POSITION_3F);
	}
	
	public Basic3DShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		super(identifier, vertexShaderIdentifier, fragmentShaderIdentifier, VertexElement.POSITION_3F);
	}
	
	@Override
	public IndicesDrawBuffer createBasicDrawBuffer(boolean color, boolean texcoords) {
		return new IndicesDrawBuffer(this, BASIC3D, CollectionUtils.arrayStringConditional(VERTEX_ATTRIBS, new boolean[]{true, color, texcoords}));
	}
	
}
