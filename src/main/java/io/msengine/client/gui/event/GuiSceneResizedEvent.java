package io.msengine.client.gui.event;

public class GuiSceneResizedEvent extends GuiEvent {

	private final int width;
	private final int height;
	
	public GuiSceneResizedEvent(int width, int height) {
		
		this.width = width;
		this.height = height;
		
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
}
