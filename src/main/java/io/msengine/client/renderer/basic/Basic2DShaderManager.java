package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.VertexElement;
import io.sutil.CollectionUtils;

import static io.msengine.client.renderer.vertex.type.BasicFormat.*;

public class Basic2DShaderManager extends BasicShaderManager {
	
	private static final String[] VERTEX_ATTRIBS = {BASIC2D_POSITION, BASIC_COLOR, BASIC_TEX_COORD};
	
	public Basic2DShaderManager(String identifier) {
		super(identifier, "basic2d", VertexElement.POSITION_2F);
	}
	
	@Override
	public IndicesDrawBuffer createBasicDrawBuffer(boolean color, boolean texcoords) {
		return new IndicesDrawBuffer(this, BASIC2D, CollectionUtils.arrayStringConditional(VERTEX_ATTRIBS, new boolean[]{true, color, texcoords}));
	}
	
}
