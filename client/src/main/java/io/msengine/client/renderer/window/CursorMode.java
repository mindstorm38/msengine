package io.msengine.client.renderer.window;

import org.lwjgl.glfw.GLFW;

public enum CursorMode {
	
	NORMAL (GLFW.GLFW_CURSOR_NORMAL),
	HIDDEN (GLFW.GLFW_CURSOR_HIDDEN),
	GRABBED (GLFW.GLFW_CURSOR_DISABLED);
	
	public final int nativ;
	
	CursorMode(int nativ) {
		this.nativ = nativ;
	}
	
}
