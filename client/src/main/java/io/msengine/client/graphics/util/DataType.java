package io.msengine.client.graphics.util;

import static org.lwjgl.opengl.GL11.*;

public class DataType {
	
	public static final DataType BYTE = new DataType(GL_BYTE, 1);
	public static final DataType UNSIGNED_BYTE = new DataType(GL_UNSIGNED_BYTE, 1);
	
	public static final DataType SHORT = new DataType(GL_SHORT, 2);
	public static final DataType UNSIGNED_SHORT = new DataType(GL_UNSIGNED_SHORT, 2);
	
	public static final DataType INT = new DataType(GL_INT, 4);
	public static final DataType UNSIGNED_INT = new DataType(GL_UNSIGNED_INT, 4);
	
	public static final DataType FLOAT = new DataType(GL_FLOAT, 4);
	
	// Class //

	public final int value;
	public final int size;
	
	public DataType(int value, int size) {
		this.value = value;
		this.size = size;
	}

}
