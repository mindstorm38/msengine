package io.msengine.client.window;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkApplicationInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;

/**
 * <b>W.I.P (DO NOT USE)</b>
 */
public class VulkanWindowBuilder extends WindowBuilder<VulkanWindow, VulkanWindowBuilder> {
	
	private String applicationName = "Hello world";
	private int applicationVersion = 0;
	
	private String engineName = "MSE";
	private int engineVersion = VK11.VK_MAKE_VERSION(1, 0, 0);
	
	private int apiVersion = VK.getInstanceVersionSupported();
	
	@Override
	protected void setupHints() {
		
		if (!glfwVulkanSupported()) {
			throw new IllegalStateException("Vulkan is not supported.");
		}
		
		super.setupHints();
		
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		
	}
	
	@Override
	protected VulkanWindow create(long id) {
		
		PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
		
		if (requiredExtensions == null) {
			throw new IllegalStateException("Failed to create vulkan window.");
		}
		
		List<Long> finalExtensions = new ArrayList<>();
		
		for (int i = 0; i < requiredExtensions.capacity(); ++i) {
			finalExtensions.add(requiredExtensions.get());
		}
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			ByteBuffer appName = stack.UTF8(this.applicationName);
			ByteBuffer engineName = stack.UTF8(this.engineName);
			
			VkApplicationInfo app = VkApplicationInfo.mallocStack(stack)
					.sType(VK11.VK_STRUCTURE_TYPE_APPLICATION_INFO)
					.pNext(0L)
					.pApplicationName(appName)
					.applicationVersion(this.applicationVersion)
					.pEngineName(engineName)
					.engineVersion(this.engineVersion)
					.apiVersion(this.apiVersion);
			
		}
		
		return new VulkanWindow(id);
	}
	
	// Attributes //
	
	public VulkanWindowBuilder withApplicationName(String name) {
		this.applicationName = Objects.requireNonNull(name);
		return this;
	}
	
	public VulkanWindowBuilder withApplicationVersion(int major, int minor, int patch) {
		this.applicationVersion = VK11.VK_MAKE_VERSION(major, minor, patch);
		return this;
	}
	
	public VulkanWindowBuilder withEngineName(String name) {
		this.engineName = Objects.requireNonNull(name);
		return this;
	}
	
	public VulkanWindowBuilder withEngineVersion(int major, int minor, int patch) {
		this.engineVersion = VK11.VK_MAKE_VERSION(major, minor, patch);
		return this;
	}
	
	public VulkanWindowBuilder withVersion(int apiVersion) {
		this.apiVersion = apiVersion;
		return this;
	}
	
}
