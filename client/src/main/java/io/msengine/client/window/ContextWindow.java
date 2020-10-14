package io.msengine.client.window;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * Base context window for OpenGL or OpenGL ES (not yet implemented).
 */
public abstract class ContextWindow extends Window {
	
	ContextWindow(long id) {
		super(id);
	}
	
	public void makeContextCurrent() {
		glfwMakeContextCurrent(this.checkId());
	}
	
	public static void detachCurrentContext() {
		glfwMakeContextCurrent(0L);
	}

	public void swapBuffers() {
		glfwSwapBuffers(this.checkId());
	}

	public static void setVSync(boolean vsync) {
		glfwSwapInterval(vsync ? 1 : 0);
	}

}
