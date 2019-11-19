package io.msengine.client.renderer.vertex.type;

import static io.msengine.client.renderer.vertex.VertexElement.*;

import io.msengine.client.renderer.vertex.VertexArrayFormat;

public class FramebufferDisplayFormat extends VertexArrayFormat {
	
	public static final FramebufferDisplayFormat FRAMEBUFFER_DISPLAY = new FramebufferDisplayFormat();
	
	public static final String FRAMEBUFFER_DISPLAY_POSITION	 = POSITION_2F.getIdentifier();
	public static final String FRAMEBUFFER_DISPLAY_TEX_COORD = TEX_COORD_2F.getIdentifier();
	
	private FramebufferDisplayFormat() {
		super(POSITION_2F, TEX_COORD_2F);
	}
	
}
