package io.msengine.client.graphics.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;

public class DataType extends Symbol {
	
	public static final DataType BYTE = new DataType(GL_BYTE, 1);
	public static final DataType UBYTE = new DataType(GL_UNSIGNED_BYTE, 1);
	
	public static final DataType SHORT = new DataType(GL_SHORT, 2);
	public static final DataType USHORT = new DataType(GL_UNSIGNED_SHORT, 2);
	
	public static final DataType INT = new DataType(GL_INT, 4);
	public static final DataType UINT = new DataType(GL_UNSIGNED_INT, 4);
	
	public static final DataType FLOAT = new DataType(GL_FLOAT, 4);
	public static final DataType HFLOAT = new DataType(GL_HALF_FLOAT, 2);

	// Class //

	public final int size;
	
	public DataType(int value, int size) {
		super(value);
		this.size = size;
	}

}
