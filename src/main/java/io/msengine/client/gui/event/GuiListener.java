package io.msengine.client.gui.event;

public interface GuiListener<E extends GuiEvent<?>> {

	public void guiEvent(E event);
	
}
