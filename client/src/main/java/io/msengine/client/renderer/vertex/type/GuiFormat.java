package io.msengine.client.renderer.vertex.type;

import io.msengine.client.renderer.vertex.VertexArrayFormat;

import static io.msengine.client.renderer.vertex.VertexElement.*;

@Deprecated
public class GuiFormat extends VertexArrayFormat {

	public static final GuiFormat GUI = new GuiFormat();
	
	public static final String GUI_POSITION  = POSITION_2F.getIdentifier();
	public static final String GUI_COLOR     = COLOR_4F.getIdentifier();
	public static final String GUI_TEX_COORD = TEX_COORD_2F.getIdentifier();
	
	private GuiFormat() {
		super(POSITION_2F, COLOR_4F, TEX_COORD_2F);
	}
	
}
