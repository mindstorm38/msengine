package io.msengine.client.graphics.buffer;

import io.msengine.client.graphics.util.BufferUsage;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

public class IndexedBufferArray extends BufferArray {
	
	private int ibo;
	private int indicesCount;
	
	public IndexedBufferArray(int vao, int[] vbos) {
		super(vao, vbos);
		this.ibo = glGenBuffers();
	}
	
	/**
	 * Get indices count used by {@link #draw(int)}.
	 * @return Indices count.
	 */
	public int getIndicesCount() {
		return this.indicesCount;
	}
	
	/**
	 * Set indices count used by {@link #draw(int)}.
	 * @param count Indices count.
	 * @return Indices count.
	 */
	public int setIndicesCount(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Can't set a count less than 0.");
		}
		return this.indicesCount = count;
	}
	
	/**
	 * Bind the <u>I</u>ndices Element Array <u>B</u>uffer <u>O</u>bject (IBO).
	 */
	public void bindIbo() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ibo);
	}
	
	/**
	 * Allocate data in IBO for future call to {@link #uploadIboSubData(long, IntBuffer)}.
	 * @param size Number of bytes to allocate.
	 * @param usage Buffer usage.
	 */
	public void allocateIboData(long size, BufferUsage usage) {
		this.bindIbo();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, usage.value);
	}
	
	/**
	 * Upload data to IBO.
	 * @param data {@link IntBuffer} data to upload.
	 * @param usage Buffer usage.
	 */
	public void uploadIboData(IntBuffer data, BufferUsage usage) {
		this.bindIbo();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage.value);
	}
	
	/**
	 * Upload data to IBO in allocated space given by {@link #allocateIboData(long, BufferUsage)}.
	 * @param offset Number of bytes to offset in allocated space.
	 * @param data {@link IntBuffer} data to upload.
	 */
	public void uploadIboSubData(long offset, IntBuffer data) {
		this.bindIbo();
		glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data);
	}
	
	@Override
	public void draw(int primitiveType) {
		if (this.indicesCount != 0) {
			this.bindVao();
			glDrawElements(primitiveType, this.indicesCount, GL_UNSIGNED_INT, 0);
			unbindVao();
		}
	}
	
	@Override
	public void close() {
		
		super.close();
		
		if (glIsBuffer(this.ibo)) {
			glDeleteBuffers(this.ibo);
			this.ibo = 0;
		}
		
	}
	
}
