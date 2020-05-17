package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL11.*;

/**
 * 
 * Enumeration of OpenGL data types
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public enum DataType {
	
	UBYTE ( GL_UNSIGNED_BYTE, 1, "UB" ),
	BYTE ( GL_BYTE, 1, "B" ),
	USHORT ( GL_UNSIGNED_SHORT, 2, "US" ),
	SHORT ( GL_SHORT, 2, "S" ),
	UINT ( GL_UNSIGNED_INT, 4, "UI" ),
	INT ( GL_INT, 4, "I" ),
	FLOAT ( GL_FLOAT, 4, "F" );
	
	public final int i;
	public final int size;
	public final String suffix;
	
	DataType(int i, int size, String suffix) {
		
		this.i = i;
		this.size = size;
		this.suffix = suffix;
		
	}
	
}
