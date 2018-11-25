package io.msengine.client.renderer.vertex;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;

import java.nio.IntBuffer;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.util.BufferUsage;

/**
 * 
 * @author Mindstorm38
 *
 */
public class IndicesDrawBuffer extends DrawBuffer {

	// Class \\
	
	private final int ibo;
	
	private int indicesCount;
	
	public IndicesDrawBuffer(ShaderManager shaderManager, VertexArrayFormat format, String...enabledVertexAttribsIdentifiers) {
		
		super( shaderManager, format, enabledVertexAttribsIdentifiers );
		
		this.ibo = glGenBuffers();
		
		this.indicesCount = 0;
		
	}
	
	// - Indices count
	
	/**
	 * Get indices count used by {@link #drawElements(int)}
	 * @return Indices count
	 */
	public int getIndicesCount() {
		return this.indicesCount;
	}
	
	/**
	 * Set indices count used by {@link #drawElements(int)}
	 * @param indicesCount Indices count
	 * @return Indices count
	 */
	public int setIndicesCount(int indicesCount) {
		return this.indicesCount = indicesCount;
	}
	
	// - Delete

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		
		super.delete();
		
		glDeleteBuffers( this.ibo );
		
	}
	
	// - Indices Buffer Object
	
	/**
	 * Bind the <u>I</u>ndices Element Array <u>B</u>uffer <u>O</u>bject (IBO)
	 */
	public void bindIbo() {
		
		this.checkDeleted();
		
		if ( currentVBOLocation == this.ibo ) return;
		glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, currentVBOLocation = this.ibo );
		
	}
	
	/**
	 * Upload data to IBO
	 * @param data {@link IntBuffer} data to upload
	 * @param usage Buffer usage
	 */
	public void uploadIboData(IntBuffer data, BufferUsage usage) {
		this.bindIbo();
		glBufferData( GL_ELEMENT_ARRAY_BUFFER, data, usage.i );
	}
	
	/**
	 * Allocate data in IBO for future call to {@link #uploadIboSubData(long, IntBuffer)}
	 * @param size Number of bytes to allocate
	 * @param usage Buffer usage
	 */
	public void allocateIboData(long size, BufferUsage usage) {
		this.bindIbo();
		glBufferData( GL_ELEMENT_ARRAY_BUFFER, size, usage.i );
	}
	
	/**
	 * Upload data to IBO in allocated space given by {@link #allocateIboData(long, BufferUsage)}
	 * @param offset Number of bytes to offset in allocated space
	 * @param data {@link IntBuffer} data to upload
	 */
	public void uploadIboSubData(long offset, IntBuffer data) {
		this.bindIbo();
		glBufferSubData( GL_ELEMENT_ARRAY_BUFFER, offset, data );
	}
	
	// - Draw
	
	/**
	 * Draw elements using IBO and {@link #indicesCount}
	 * @param mode OpenGL primitive type
	 */
	public void drawElements(int mode) {
		
		if ( this.indicesCount == 0 ) return;
		
		this.preDraw();
		
		glDrawElements( mode, this.indicesCount, GL_UNSIGNED_INT, 0 );
		
		this.postDraw();
		
	}
	
	/**
	 * Use {@link #drawElements(int)} with {@link DrawBuffer#DEFAULT_PRIMITIVE_TYPE}
	 */
	public void drawElements() {
		this.drawElements( DEFAULT_PRIMITIVE_TYPE );
	}
	
	// - Static buffer unbinding
	
	/**
	 * Unbind IBO
	 */
	public static void unbindIbo() {
		
		glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, currentVBOLocation = 0 );
		
		currentVBODrawBuffer = null;
		currentVBOIndex = -1;
		
	}
	
}
