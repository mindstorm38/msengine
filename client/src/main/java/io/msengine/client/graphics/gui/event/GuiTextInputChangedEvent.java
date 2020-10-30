package io.msengine.client.graphics.gui.event;

public class GuiTextInputChangedEvent extends GuiEvent {
	
	private final String value;
	
	public GuiTextInputChangedEvent(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
