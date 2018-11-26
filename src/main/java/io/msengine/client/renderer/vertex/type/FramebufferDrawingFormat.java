package io.msengine.client.renderer.vertex.type;

import static io.msengine.client.renderer.vertex.VertexElement.*;

import io.msengine.client.renderer.vertex.VertexArrayFormat;

public class FramebufferDrawingFormat extends VertexArrayFormat {
	
	public static final FramebufferDrawingFormat FRAMEBUFFER_DRAWING = new FramebufferDrawingFormat();
	
	public static final String FRAMEBUFFER_DRAWING_POSITION		= POSITION_2F.getIdentifier();
	
	private FramebufferDrawingFormat() {
		
		super( POSITION_2F );
		
	}
	
}
