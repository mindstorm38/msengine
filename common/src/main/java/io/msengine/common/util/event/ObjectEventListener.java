package io.msengine.common.util.event;

public interface ObjectEventListener<E extends ObjectEvent> {
	void onEvent(E event);
}
