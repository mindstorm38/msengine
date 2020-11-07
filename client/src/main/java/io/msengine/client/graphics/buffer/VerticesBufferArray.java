package io.msengine.client.graphics.buffer;

import static org.lwjgl.opengl.GL11.glDrawArrays;

public class VerticesBufferArray extends BufferArray {
	
	private int verticesCount;
	
	public VerticesBufferArray(int vao, int[] vbos) {
		super(vao, vbos);
	}
	
	/**
	 * Get vertices count used by {@link #draw(int)}.
	 * @return Vertices count.
	 */
	public int getVerticesCount() {
		return this.verticesCount;
	}
	
	/**
	 * Set vertices count used by {@link #draw(int)}.
	 * @param count Vertices count.
	 * @return Vertices count.
	 */
	public int setVerticesCount(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Can't set a count less than 0.");
		}
		return this.verticesCount = count;
	}
	
	@Override
	public void draw(int primitiveType) {
		if (this.verticesCount != 0) {
			this.bindVao();
			glDrawArrays(primitiveType, 0, this.verticesCount);
			unbindVao();
		}
	}
	
	@Override
	public void draw(int primitiveType, int offset, int count) {
		if (count >= 0) {
			this.bindVao();
			glDrawArrays(primitiveType, offset, count);
			unbindVao();
		}
	}
	
}
