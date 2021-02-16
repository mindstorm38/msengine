package io.msengine.client.renderer.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import io.msengine.client.renderer.util.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.util.BufferUsage;

import static io.msengine.client.renderer.vertex.type.GuiFormat.*;

/**
 * @deprecated Consider using the new package {@link io.msengine.client.graphics}.
 */
@Deprecated
public class GUIMaskRectangle extends GuiMask {
	
	private boolean updateVerticesBuffer;
	
	private float x;
	private float y;
	private float width;
	private float height;
	
	public GUIMaskRectangle(float x, float y, float width, float height) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	public GUIMaskRectangle() {
		this( 0, 0, 1, 1 );
	}
	
	@Override
	public void init() {
		
		super.init();
		
		this.initBuffers();
		
	}

	private void initBuffers() {
		
		IntBuffer indicesBuffer = null;
		
		try {
			
			indicesBuffer = MemoryUtil.memAllocInt( this.buffer.setIndicesCount( 6 ) );
			
			indicesBuffer.put( 0 ).put( 1 ).put( 3 );
			indicesBuffer.put( 1 ).put( 2 ).put( 3 );
			
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.allocateVboData( GUI_POSITION, 8 << 2, BufferUsage.DYNAMIC_DRAW );
			this.buffer.uploadIboData( indicesBuffer, BufferUsage.STATIC_DRAW );
			
		} finally {
			BufferUtils.safeFree(indicesBuffer);
		}
		
		this.updateVerticesBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		FloatBuffer verticesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat( 8 );
			
			verticesBuffer.put( 0 ).put( 0 );
			verticesBuffer.put( 0 ).put( this.height );
			verticesBuffer.put( this.width ).put( this.height );
			verticesBuffer.put( this.width ).put( 0 );
			
			verticesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboSubData( GUI_POSITION, 0, verticesBuffer );
			
		} finally {
			BufferUtils.safeFree(verticesBuffer);
		}
		
		this.updateVerticesBuffer = false;
		
	}
	
	@Override
	public void draw() {
		
		if ( this.updateVerticesBuffer )
			this.updateVerticesBuffer();
		
		this.model.push().translate(this.x, this.y).apply();
		super.draw();
		this.model.pop();
		
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public void setWidth(float width) {
		
		if ( width == this.width )
			return;
		
		this.width = width;
		this.updateVerticesBuffer = true;
		
	}
	
	public void setHeight(float height) {
		
		if ( height == this.height )
			return;
		
		this.height = height;
		this.updateVerticesBuffer = true;
		
	}
	
	public void setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public void setPositionSize(float x, float y, float width, float height) {
		
		this.x = x;
		this.y = y;
		
		if ( width == this.width && height == this.height )
			return;
		
		this.width = width;
		this.height = height;
		this.updateVerticesBuffer = true;
		
	}
	
}
