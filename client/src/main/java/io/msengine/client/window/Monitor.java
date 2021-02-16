package io.msengine.client.window;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Monitor {
	
	static {
		WindowHandler.init();
	}
	
	public static List<Monitor> getMonitors() {
		WindowHandler.ensureReady();
		PointerBuffer buf = glfwGetMonitors();
		List<Monitor> monitors = new ArrayList<>();
		if (buf != null) {
			while (buf.remaining() != 0) {
				monitors.add(new Monitor(buf.get()));
			}
		}
		return monitors;
	}
	
	public static Monitor getPrimaryMonitor() {
		WindowHandler.ensureReady();
		return new Monitor(glfwGetPrimaryMonitor());
	}
	
	private final long handle;
	
	private Monitor(long handle) {
		this.handle = handle;
	}
	
	public long getHandle() {
		return this.handle;
	}
	
	public String getName() {
		return glfwGetMonitorName(this.handle);
	}
	
	public GLFWVidMode getVideoMode() {
		return glfwGetVideoMode(this.handle);
	}
	
	public void setGamma(float gamma) {
		glfwSetGamma(this.handle, gamma);
	}
	
	@Override
	public String toString() {
		return "Monitor<" + this.getName() + ">";
	}
	
}
