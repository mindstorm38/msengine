package io.msengine.client.renderer.framebuffer;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.vertex.FrameDrawBuffer;

import static io.msengine.client.renderer.vertex.type.FramebufferDisplayFormat.*;

/**
 * @deprecated Not yet re-implemented in {@link io.msengine.client.graphics} packages.
 */
@Deprecated
public class FramebufferDisplayDrawBuffer extends FrameDrawBuffer {

	public FramebufferDisplayDrawBuffer(FramebufferDisplayShaderManager shaderManager) {
		
		super( shaderManager, FRAMEBUFFER_DISPLAY, FRAMEBUFFER_DISPLAY_POSITION, FRAMEBUFFER_DISPLAY_POSITION, FRAMEBUFFER_DISPLAY_TEX_COORD );
		
		FloatBuffer texCoordsBuffer = null;
		
		try {
			
			texCoordsBuffer = MemoryUtil.memAllocFloat( 8 );
			
			texCoordsBuffer.put( 0 ).put( 0 );
			texCoordsBuffer.put( 0 ).put( 1 );
			texCoordsBuffer.put( 1 ).put( 1 );
			texCoordsBuffer.put( 1 ).put( 0 );
			
			texCoordsBuffer.flip();
			
			this.uploadVboData( FRAMEBUFFER_DISPLAY_TEX_COORD, texCoordsBuffer, BufferUsage.STATIC_DRAW );
			
		} finally {
			
			if ( texCoordsBuffer != null ) MemoryUtil.memFree( texCoordsBuffer );
			
		}
		
	}

}
