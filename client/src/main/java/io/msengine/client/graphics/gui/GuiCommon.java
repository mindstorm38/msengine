package io.msengine.client.graphics.gui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GuiCommon {
	
	/**
	 * <p>Put indices for drawing a square :<br>
	 * <code>0/1/3  1/2/3</code></p>
	 * @param idx The origin index.
	 * @param buf The buffer containing the indices.
	 */
	public static void putSquareIndices(int idx, IntBuffer buf) {
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
