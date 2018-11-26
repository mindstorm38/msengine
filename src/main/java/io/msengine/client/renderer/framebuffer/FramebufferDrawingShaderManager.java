package io.msengine.client.renderer.framebuffer;

import static io.msengine.client.renderer.vertex.VertexElement.*;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.shader.ShaderUniform;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.common.game.GameTimed;

public class FramebufferDrawingShaderManager extends ShaderManager implements GameTimed {

	public static final String FRAMEBUFFER_DRAWING_RESOLUTION		= "resolution";
	public static final String FRAMEBUFFER_DRAWING_TIME				= "time";
	
	protected FramebufferDrawingDrawBuffer buffer;
	
	public FramebufferDrawingShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		
		super( identifier, vertexShaderIdentifier, fragmentShaderIdentifier );
		
		this.registerAttribute( POSITION_2F );
		
		this.registerUniform( FRAMEBUFFER_DRAWING_RESOLUTION, ShaderValueType.VEC2 );
		this.registerUniform( FRAMEBUFFER_DRAWING_TIME, ShaderValueType.FLOAT );
		
		this.buffer = null;
		
	}
	
	public FramebufferDrawingShaderManager(String identifier, String fragmentShaderIdentifier) {
		this( identifier, "fb_drawing", fragmentShaderIdentifier );
	}
	
	public FramebufferDrawingShaderManager(String identifier) {
		this( identifier, identifier );
	}
	
	public ShaderUniform getResolutionUniform() {
		return this.getShaderUniformOrDefault( FRAMEBUFFER_DRAWING_RESOLUTION );
	}
	
	public ShaderUniform getTimeUniform() {
		return this.getShaderUniformOrDefault( FRAMEBUFFER_DRAWING_TIME );
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
	
	public FramebufferDrawingDrawBuffer getBuffer() {
		return this.buffer;
	}

	protected FramebufferDrawingDrawBuffer createDrawBuffer() {
		return new FramebufferDrawingDrawBuffer( this );
	}

	@Override
	public void setTime(double time) {
		this.getTimeUniform().set( (float) time );
	}

}
