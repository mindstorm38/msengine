package io.msengine.client.gui.event;

public interface GuiListener<E extends GuiEvent> {

	void guiEvent(E event);
	
}
