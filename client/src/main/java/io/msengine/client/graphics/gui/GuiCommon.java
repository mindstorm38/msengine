package io.msengine.client.graphics.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GuiCommon {
	
	/**
	 * <p>Put indices for drawing a square :<br>
	 * <code>idx+0/idx+1/idx+3  idx+1/idx+2/idx+3</code></p>
	 * @param buf The buffer containing the indices.
	 * @param idx The origin index.
	 */
	public static void putSquareIndices(IntBuffer buf, int idx) {
		buf.put(idx).put(idx + 1).put(idx + 3);
		buf.put(idx + 1).put(idx + 2).put(idx + 3);
	}
	
	/**
	 * <p>Put indices for drawing a square :<br>
	 * <code>0/1/3  1/2/3</code></p>
	 * @param buf The buffer containing the indices.
	 */
	public static void putSquareIndices(IntBuffer buf) {
		buf.put(0).put(1).put(3);
		buf.put(1).put(2).put(3);
	}
	
	public static void putSquareIndices(IntBuffer buf, int i0, int i1, int i2, int i3) {
		buf.put(i0).put(i1).put(i3);
		buf.put(i1).put(i2).put(i3);
	}
	
	/**
	 * <p>Put vertices for drawing a square :<br>
	 * <code>0/0  0/height  width/height  width/0</code></p>
	 * @param buf The buffer containing the vertices.
	 * @param width The square width.
	 * @param height The square height.
	 */
	public static void putSquareVertices(FloatBuffer buf, float width, float height) {
		buf.put(0).put(0);
		buf.put(0).put(height);
		buf.put(width).put(height);
		buf.put(width).put(0);
	}

}
