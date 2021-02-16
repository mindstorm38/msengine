package io.msengine.example.window;

import io.msengine.client.window.GLWindow;
import io.msengine.client.window.GLWindowBuilder;
import io.msengine.client.window.Monitor;
import org.lwjgl.glfw.GLFWVidMode;

public class WindowExample {
	
	public static void main(String[] args) throws InterruptedException {
		
		System.out.println("Listing monitors :");
		Monitor.getMonitors().forEach(monitor -> {
			GLFWVidMode mode = monitor.getVideoMode();
			System.out.println("- " + monitor.getName() + " (" + mode.width() + "/" + mode.height() + ")");
		});
		
		System.out.println("Starting window ...");
		
		GLWindow window = new GLWindowBuilder()
				.withTitle("[MSE] Test title")
				.build();
		
		GLWindow subWindow = new GLWindowBuilder()
				.withTitle("[MSE] Sub Window")
				.shareOther(window)
				.build();
		
		window.show();
		subWindow.show();
		
		while (!window.shouldClose()) {
			if (subWindow.shouldClose()) {
				subWindow.close();
			}
			Thread.sleep(10);
			GLWindow.pollEvents();
		}
		window.close();
		
		System.out.println("Window closed.");
		
	}
	
}
