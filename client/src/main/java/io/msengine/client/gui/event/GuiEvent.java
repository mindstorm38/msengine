package io.msengine.client.gui.event;

import io.msengine.client.gui.GuiObject;

@Deprecated
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
	
	public boolean isOrigin(GuiObject obj) {
		return this.origin == obj;
	}
	
}
