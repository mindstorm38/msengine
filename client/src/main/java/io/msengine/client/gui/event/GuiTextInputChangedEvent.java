package io.msengine.client.gui.event;

@Deprecated
public class GuiTextInputChangedEvent extends GuiEvent {
	
	private final String value;
	
	public GuiTextInputChangedEvent(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
