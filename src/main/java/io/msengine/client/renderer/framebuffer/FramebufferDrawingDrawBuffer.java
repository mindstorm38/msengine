package io.msengine.client.renderer.framebuffer;

import io.msengine.client.renderer.vertex.FrameDrawBuffer;
import io.msengine.client.renderer.vertex.VertexArrayFormat;

import static io.msengine.client.renderer.vertex.type.FramebufferDrawingFormat.*;

public class FramebufferDrawingDrawBuffer extends FrameDrawBuffer {

	protected FramebufferDrawingDrawBuffer(FramebufferDrawingShaderManager shaderManager, VertexArrayFormat format, String positionVbo, String...enabledVertexAttribsIdentifiers) {
		super( shaderManager, format, positionVbo, enabledVertexAttribsIdentifiers );
	}
	
	public FramebufferDrawingDrawBuffer(FramebufferDrawingShaderManager shaderManager) {
		super( shaderManager, FRAMEBUFFER_DRAWING, FRAMEBUFFER_DRAWING_POSITION, FRAMEBUFFER_DRAWING_POSITION );
	}

}
