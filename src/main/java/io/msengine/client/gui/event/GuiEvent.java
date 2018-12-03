package io.msengine.client.gui.event;

import io.msengine.client.gui.GuiObject;

public class GuiEvent<O extends GuiObject> {
	
	private O origin;
	
	public GuiEvent() {
		
		this.origin = null;
		
	}
	
	public void setOrigin(O origin) {
		this.origin = origin;
	}
	
	public O getOrigin() {
		return this.origin;
	}
	
}
