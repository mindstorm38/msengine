package io.msengine.client.window;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindowBuilder extends ContextWindowBuilder<GLWindow, GLWindowBuilder> {
	
	public GLWindowBuilder() {
		super(GLWindow.MIN_VERSION_MINOR, GLWindow.MIN_VERSION_MAJOR);
	}
	
	@Override
	protected void setupHints() {
		super.setupHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
	}
	
	protected GLWindow create(long id) {
		long lastContext = glfwGetCurrentContext();
		glfwMakeContextCurrent(id);
		GLCapabilities capabilities = GL.createCapabilities();
		glfwMakeContextCurrent(lastContext);
		return new GLWindow(id, capabilities);
	}
	
}