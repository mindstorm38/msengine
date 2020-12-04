package io.msengine.client.gui.event;

@Deprecated
public interface GuiListener<E extends GuiEvent> {

	void guiEvent(E event);
	
}
