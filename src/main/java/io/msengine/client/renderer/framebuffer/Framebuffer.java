package io.msengine.client.renderer.framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.renderer.texture.TextureObject;
import io.msengine.client.renderer.window.Window;

public class Framebuffer implements	ShaderSamplerObject {
	
	// Constants \\
	
	private static final int[] COLOR_ATTACHMENT = {
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1,
			GL_COLOR_ATTACHMENT2,
			GL_COLOR_ATTACHMENT3,
			GL_COLOR_ATTACHMENT4,
			GL_COLOR_ATTACHMENT5,
			GL_COLOR_ATTACHMENT6,
			GL_COLOR_ATTACHMENT7,
			GL_COLOR_ATTACHMENT8,
			GL_COLOR_ATTACHMENT9,
			GL_COLOR_ATTACHMENT10,
			GL_COLOR_ATTACHMENT11,
			GL_COLOR_ATTACHMENT12,
			GL_COLOR_ATTACHMENT13,
			GL_COLOR_ATTACHMENT14,
			GL_COLOR_ATTACHMENT15
	};
	
	// Static \\
	
	public static void unbind() {
		glBindFramebuffer( GL_FRAMEBUFFER, 0 );
	}
	
	private static void checkComplete() {
		
		int i = glCheckFramebufferStatus( GL_FRAMEBUFFER );
		
		if ( i != GL_FRAMEBUFFER_COMPLETE ) {
			
			switch ( i ) {
				case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
					throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
				case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
					throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
				case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
					throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
				case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
					throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			}
			
			throw new RuntimeException( "Unknow status : " + i );
			
		}
		
	}
	
	public static int getColorAttachment(int idx) {
		if ( idx < 0 || idx >= COLOR_ATTACHMENT.length ) throw new IllegalArgumentException( "Invalid color attachment index " + idx + ". Must be between 0 and " + ( COLOR_ATTACHMENT.length - 1 ) + "." );
		return COLOR_ATTACHMENT[ idx ];
	}
	
	// Class \\
	
	protected int width;
	protected int height;
	
	protected final float[] color;
	
	protected int id;
	protected TextureObject[] colorbuffers;
	protected int renderbufferId;
	
	protected boolean deleted;
	
	public Framebuffer(int colorAttachmentsCount) {
		
		if ( colorAttachmentsCount < 1 || colorAttachmentsCount > COLOR_ATTACHMENT.length ) throw new IllegalArgumentException("Invalid Framebuffer color attachments size"); 
		
		this.id = -1;
		this.setColorAttachmentCount( colorAttachmentsCount );
		this.renderbufferId = -1;
		
		this.color = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		
		this.deleted = false;
		
		// this.create( width, height );
		
	}
	
	public Framebuffer() {
		this( 1 );
	}
	
	public boolean usable() {
		return !this.deleted;
	}
	
	public boolean deleted() {
		return this.deleted;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public float[] getColor() {
		return this.color;
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
		this.color[3] = a;
	}
	
	public int getColorAttachmentCount() {
		return this.colorbuffers.length;
	}
	
	public void setColorAttachmentCount(int colorAttachmentsCount) {
		this.checkConfigurable();
		this.colorbuffers = new TextureObject[ colorAttachmentsCount ];
	}
	
	public Framebuffer create(Window window) {
		return this.create( window.getWidth(), window.getHeight() );
	}
	
	/**
	 * Create (or re-create) the framebuffer object
	 * @param width New framebuffer width
	 * @param height New framebuffer height
	 */
	public Framebuffer create(int width, int height) {
		
		if ( !this.deleted ) {
			
			this.delete();
			this.deleted = false;
			
		}
		
		this.width = width;
		this.height = height;
		
		// Generating framebuffer id
		this.id = glGenFramebuffers();
		
		// Binding framebuffer
		this.bind();
		
		// Generating depth / stencil buffer
		this.renderbufferId = glGenRenderbuffers();
		glBindRenderbuffer( GL_RENDERBUFFER, this.renderbufferId );
		glRenderbufferStorage( GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height );
		glBindRenderbuffer( GL_RENDERBUFFER, 0 );
		
		glFramebufferRenderbuffer( GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, this.renderbufferId );
		
		// Generating color buffers and attaching to framebuffer with specific color attachment
		for ( int i = 0; i < this.colorbuffers.length; i++ )
			glFramebufferTexture2D( GL_FRAMEBUFFER, getColorAttachment( i ), GL_TEXTURE_2D, ( this.colorbuffers[ i ] = new TextureObject( width, height ) ).getId(), 0 );
		
		// Checking framebuffer validity
		try {
			
			checkComplete();
			
		} catch (RuntimeException e) {
			
			this.delete();
			throw e;
			
		}
		
		this.setColorBuffers( null );
		
		// Resetting all
		this.clear();
		
		return this;
		
	}
	
	public void setColorBuffers(int[] colorbuffers) {
		
		glBindFramebuffer( GL_FRAMEBUFFER, this.id );
		
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			
			IntBuffer buffer = stack.mallocInt( colorbuffers == null ? this.colorbuffers.length : colorbuffers.length );
			
			if ( colorbuffers == null ) {
				
				for ( int i = 0; i < this.colorbuffers.length; i++ )
					buffer.put( getColorAttachment( i ) );
				
			} else {
				
				for ( int i = 0; i < colorbuffers.length; i++ )
					buffer.put( getColorAttachment( colorbuffers[ i ] ) );
				
			}
			
			buffer.flip();
			
			GL20.glDrawBuffers( buffer );
			
		}
		
		unbind();
		
	}
	
	public void bind() {
		
		this.checkDeleted();
		glBindFramebuffer( GL_FRAMEBUFFER, this.id );
		
	}
	
	public void bind(boolean updateViewport) {
		this.bind();
		if ( updateViewport ) glViewport( 0, 0, this.width, this.height );
	}
	
	/**
	 * Clear the framebuffer color buffer and (if useDepth) depth buffer
	 */
	public void clear(boolean unbind) {
		
		this.bind( true );
		
		// glClearBufferfi( GL_DEPTH_STENCIL, drawbuffer, depth, stencil);
		
		glClearColor( this.color[0], this.color[1], this.color[2], this.color[3] );
		glClear( GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT );
		
		if ( unbind ) Framebuffer.unbind();
		
	}
	
	public void clear() {
		this.clear( false );
	}
	
	public void delete() {
		
		if ( this.deleted ) return;
		
		TextureObject.unbind();
		unbind();
		
		if ( this.renderbufferId > -1 ) {
			
			glDeleteRenderbuffers( this.renderbufferId );
			this.renderbufferId = -1;
			
		}
		
		for ( int i = 0; i < this.colorbuffers.length; i++ ) {
			
			if ( this.colorbuffers[ i ] != null ) {
				
				this.colorbuffers[ i ].delete();
				this.colorbuffers[ i ] = null;
			
			}
			
		}
		
		this.colorbuffers = null;
		
		if ( this.id > -1 ) {
			
			glDeleteFramebuffers( this.id );
			this.id = -1;
			
		}
		
		this.deleted = true;
		
	}
	
	public boolean isDeleted() {
		return this.deleted;
	}
	
	private void checkDeleted() {
		if ( this.deleted ) throw new IllegalStateException("Unusable Framebuffer because it has been deleted.");
	}
	
	private void checkConfigurable() {
		if ( !this.deleted ) throw new IllegalStateException("Can't configure this Framebuffer, it's not deleted.");
	}
	
	public TextureObject getColorbuffer(int idx) {
		return this.colorbuffers[ idx ];
	}
	
	public TextureObject getColorbuffer() {
		return this.getColorbuffer( 0 );
	}
	
	@Override
	public int getSamplerId() {
		return this.colorbuffers[ 0 ].getSamplerId();
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		if ( !this.deleted ) this.delete();
		
	}

}
