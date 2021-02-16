package io.msengine.client.window;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

public abstract class ContextWindowBuilder<W extends ContextWindow, SELF extends WindowBuilder<W, SELF>> extends WindowBuilder<W, SELF> {
	
	private long share = 0L;
	
	private int versionMajor;
	private int versionMinor;
	
	public ContextWindowBuilder(int defaultVersionMajor, int defaultVersionMinor) {
		this.versionMajor = defaultVersionMajor;
		this.versionMinor = defaultVersionMinor;
	}
	
	@Override
	protected void setupHints() {
		super.setupHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, this.versionMajor);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, this.versionMinor);
	}
	
	@Override
	protected long createWindow() {
		return glfwCreateWindow(this.width, this.height, this.title, this.monitor, this.share);
	}
	
	// Attributes //
	
	@SuppressWarnings("unchecked")
	public SELF shareOther(W otherWindow) {
		this.share = otherWindow.getId();
		return (SELF) this;
	}
	
	@SuppressWarnings("unchecked")
	public SELF withVersion(int major, int minor) {
		this.versionMajor = major;
		this.versionMinor = minor;
		return (SELF) this;
	}

}
