package io.msengine.common.util.event;

public interface EventListener<E extends BaseEvent> {
	
	void event(E event);
	
}
