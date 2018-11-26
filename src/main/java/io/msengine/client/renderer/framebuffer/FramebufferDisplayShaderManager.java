package io.msengine.client.renderer.framebuffer;

import static io.msengine.client.renderer.vertex.VertexElement.*;

public class FramebufferDisplayShaderManager extends FramebufferDrawingShaderManager {

	public static final String FRAMEBUFFER_DISPLAY_TEXTURE_SAMPLER	= "texture_sampler";
	public static final String FRAMEBUFFER_DISPLAY_RESOLUTION		= FRAMEBUFFER_DRAWING_RESOLUTION;
	public static final String FRAMEBUFFER_DISPLAY_TIME				= FRAMEBUFFER_DRAWING_TIME;
	
	public FramebufferDisplayShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		
		super( identifier, vertexShaderIdentifier, fragmentShaderIdentifier );
		
		this.registerAttribute( TEX_COORD_2F );
		
		this.registerSampler( FRAMEBUFFER_DISPLAY_TEXTURE_SAMPLER );
		
	}
	
	public FramebufferDisplayShaderManager(String identifier, String fragmentShaderIdentifier) {
		this( identifier, "fb", fragmentShaderIdentifier );
	}
	
	public FramebufferDisplayShaderManager(String identifier) {
		this( identifier, "fb", "fb" );
	}
	
	public void setFramebuffer(Framebuffer framebuffer) {
		this.setSamplerObject( FRAMEBUFFER_DISPLAY_TEXTURE_SAMPLER, framebuffer );
	}
	
	@Override
	protected FramebufferDrawingDrawBuffer createDrawBuffer() {
		return new FramebufferDisplayDrawBuffer( this );
	}
	
}
