package io.msengine.client.graphics.util;

import static org.lwjgl.opengl.GL15.*;

public class BufferUsage {
	
	public static final BufferUsage STATIC_DRAW = new BufferUsage(GL_STATIC_DRAW);
	public static final BufferUsage STATIC_READ = new BufferUsage(GL_STATIC_READ);
	public static final BufferUsage STATIC_COPY = new BufferUsage(GL_STATIC_COPY);
	public static final BufferUsage DYNAMIC_DRAW = new BufferUsage(GL_DYNAMIC_DRAW);
	public static final BufferUsage DYNAMIC_READ = new BufferUsage(GL_DYNAMIC_READ);
	public static final BufferUsage DYNAMIC_COPY = new BufferUsage(GL_DYNAMIC_COPY);
	public static final BufferUsage STREAM_DRAW = new BufferUsage(GL_STREAM_DRAW);
	public static final BufferUsage STREAM_READ = new BufferUsage(GL_STREAM_READ);
	public static final BufferUsage STREAM_COPY = new BufferUsage(GL_STREAM_COPY);
	
	// Class //
	
	public final int value;
	
	public BufferUsage(int value) {
		this.value = value;
	}

}
