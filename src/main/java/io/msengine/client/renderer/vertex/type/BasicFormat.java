package io.msengine.client.renderer.vertex.type;

import io.msengine.client.renderer.vertex.VertexArrayFormat;
import io.msengine.client.renderer.vertex.VertexElement;

import static io.msengine.client.renderer.vertex.VertexElement.*;

public class BasicFormat extends VertexArrayFormat {
	
	public static final BasicFormat BASIC2D = new BasicFormat(POSITION_2F);
	public static final BasicFormat BASIC3D = new BasicFormat(POSITION_3F);
	
	public static final String BASIC_COLOR       = COLOR_3F.getIdentifier();
	public static final String BASIC_TEX_COORD   = TEX_COORD_2F.getIdentifier();
	public static final String BASIC2D_POSITION  = POSITION_2F.getIdentifier();
	public static final String BASIC3D_POSITION  = POSITION_3F.getIdentifier();
	
	private BasicFormat(VertexElement position) {
		super(position, COLOR_4F, TEX_COORD_2F);
	}
	
}
