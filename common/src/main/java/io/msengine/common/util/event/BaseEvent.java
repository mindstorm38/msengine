package io.msengine.common.util.event;

public abstract class BaseEvent {

	private EventManager<?> manager = null;

	void setManager(EventManager<?> manager) {
		this.manager = manager;
	}
	
	public EventManager<?> getManager() {
		return this.manager;
	}
	
}
