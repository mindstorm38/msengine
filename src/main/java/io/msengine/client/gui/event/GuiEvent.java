package io.msengine.client.gui.event;

import io.msengine.client.gui.GuiObject;

public class GuiEvent {
	
	private GuiObject origin;
	
	public GuiEvent() {
		
		this.origin = null;
		
	}
	
	public void setOrigin(GuiObject origin) {
		this.origin = origin;
	}
	
	public GuiObject getOrigin() {
		return this.origin;
	}
	
}
