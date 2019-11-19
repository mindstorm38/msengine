package io.msengine.client.renderer.vertex.type;

import io.msengine.client.renderer.vertex.VertexArrayFormat;

import static io.msengine.client.renderer.vertex.VertexElement.*;

public class Basic2DFormat extends VertexArrayFormat {
	
	public static final Basic2DFormat BASIC2D = new Basic2DFormat();
	
	public static final String BASIC2D_POSITION  = POSITION_2F.getIdentifier();
	public static final String BASIC2D_COLOR     = COLOR_4F.getIdentifier();
	public static final String BASIC2D_TEX_COORD = TEX_COORD_2F.getIdentifier();
	
	private Basic2DFormat() {
		super(POSITION_2F, COLOR_4F, TEX_COORD_2F);
	}
	
}
