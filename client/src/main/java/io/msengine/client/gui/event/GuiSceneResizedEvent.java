package io.msengine.client.gui.event;

import io.msengine.client.renderer.window.Window;

public class GuiSceneResizedEvent extends GuiEvent {

	private final int width;
	private final int height;
	
	public GuiSceneResizedEvent(int width, int height) {
		
		this.width = width;
		this.height = height;
		
	}
	
	public GuiSceneResizedEvent(Window window) {
		this(window.getWidth(), window.getHeight());
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
}
