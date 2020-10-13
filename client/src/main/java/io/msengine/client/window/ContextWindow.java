package io.msengine.client.window;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

/**
 * Base context window for OpenGL or OpenGL ES (not yet implemented).
 */
public abstract class ContextWindow extends Window {
	
	ContextWindow(long id, int width, int height) {
		super(id, width, height);
	}
	
	public void makeContextCurrent() {
		glfwMakeContextCurrent(this.id);
	}
	
	public static void detachCurrentContext() {
		glfwMakeContextCurrent(0L);
	}
	
}
