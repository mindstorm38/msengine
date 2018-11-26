package io.msengine.client.renderer.framebuffer;

import static io.msengine.client.renderer.vertex.VertexElement.*;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.shader.ShaderUniform;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.client.renderer.window.Window;
import io.msengine.common.game.GameTimed;

public class FramebufferDisplayShaderManager extends ShaderManager implements GameTimed {

	public static final String FRAMEBUFFER_DISPLAY_RESOLUTION		= "resolution";
	public static final String FRAMEBUFFER_DISPLAY_TIME				= "time";
	public static final String FRAMEBUFFER_DISPLAY_TEXTURE_SAMPLER	= "texture_sampler";
	
	protected FramebufferDisplayDrawBuffer buffer;
	
	public FramebufferDisplayShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		
		super( identifier, vertexShaderIdentifier, fragmentShaderIdentifier );

		this.registerAttribute( POSITION_2F );
		this.registerAttribute( TEX_COORD_2F );
		
		this.registerUniform( FRAMEBUFFER_DISPLAY_RESOLUTION, ShaderValueType.VEC2 );
		this.registerUniform( FRAMEBUFFER_DISPLAY_TIME, ShaderValueType.FLOAT );
		
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
	
	public ShaderUniform getResolutionUniform() {
		return this.getShaderUniformOrDefault( FRAMEBUFFER_DISPLAY_RESOLUTION );
	}
	
	public ShaderUniform getTimeUniform() {
		return this.getShaderUniformOrDefault( FRAMEBUFFER_DISPLAY_TIME );
	}
	
	public void setResolution(float width, float height) {
		this.getResolutionUniform().set( width, height );
	}
	
	public void setResolution(int width, int height) {
		this.getResolutionUniform().set( (float) width, (float) height );
	}
	
	public void setResolution(Window window) {
		this.setResolution( window.getWidth(), window.getHeight() );
	}
	
	@Override
	public void build() {
		
		super.build();
		
		this.deleteBuffer();
		
		this.buffer = this.createDrawBuffer();
		
	}
	
	@Override
	public void delete() {
		
		super.delete();
		
		this.deleteBuffer();
		
	}
	
	private final void deleteBuffer() {
		
		if ( this.buffer != null ) {
			
			this.buffer.delete();
			this.buffer = null;
			
		}
		
	}
	
	public void drawFramebuffer(BlendMode blendMode) {
		
		blendMode.use();
		
		this.use();
		this.buffer.drawElements();
		this.end();
		
	}
	
	public void drawFramebuffer() {
		this.drawFramebuffer( BlendMode.TRANSPARENCY );
	}
	
	public FramebufferDisplayDrawBuffer getBuffer() {
		return this.buffer;
	}

	protected FramebufferDisplayDrawBuffer createDrawBuffer() {
		return new FramebufferDisplayDrawBuffer( this );
	}

	@Override
	public void setTime(double time) {
		this.getTimeUniform().set( (float) time );
	}
	
}
