package io.msengine.client.renderer.vertex.type;

import io.msengine.client.renderer.vertex.VertexArrayFormat;

import static io.msengine.client.renderer.vertex.VertexElement.*;

public class Basic3DFormat extends VertexArrayFormat {
	
	public static final Basic3DFormat BASIC3D = new Basic3DFormat();
	
	public static final String BASIC3D_POSITION  = POSITION_3F.getIdentifier();
	public static final String BASIC3D_COLOR     = COLOR_4F.getIdentifier();
	public static final String BASIC3D_TEX_COORD = TEX_COORD_2F.getIdentifier();
	
	private Basic3DFormat() {
		super(POSITION_3F, COLOR_4F, TEX_COORD_2F);
	}
	
}
