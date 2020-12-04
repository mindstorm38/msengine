package io.msengine.client.gui.event;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GuiListenerGroup<E extends GuiEvent> {

	private final Class<E> eventClass;
	private final List<GuiListener<E>> listeners;
	
	public GuiListenerGroup(Class<E> eventClass) {
		
		this.eventClass = eventClass;
		this.listeners = new ArrayList<>();
		
	}
	
	public Class<E> getEventClass() {
		return this.eventClass;
	}
	
	public List<GuiListener<E>> getListeners() {
		return this.listeners;
	}
	
}
