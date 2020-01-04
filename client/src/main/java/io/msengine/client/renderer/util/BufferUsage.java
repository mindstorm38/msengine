package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL15.*;

/**
 * 
 * Enumeration of defaults OpenGL buffer usage
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public enum BufferUsage {
		
	STATIC_DRAW ( GL_STATIC_DRAW ),
	STATIC_READ ( GL_STATIC_READ ),
	STATIC_COPY ( GL_STATIC_COPY ),
	DYNAMIC_DRAW ( GL_DYNAMIC_DRAW ),
	DYNAMIC_READ ( GL_DYNAMIC_READ ),
	DYNAMIC_COPY ( GL_DYNAMIC_COPY ),
	STREAM_DRAW ( GL_STREAM_DRAW ),
	STREAM_READ ( GL_STREAM_READ ),
	STREAM_COPY ( GL_STREAM_COPY );
	
	public final int i;
	
	BufferUsage(int i) {
		this.i = i;
	}
	
}