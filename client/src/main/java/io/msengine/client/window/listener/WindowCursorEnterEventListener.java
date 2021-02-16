package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowCursorEnterEventListener {
	void onWindowCursorEnterEvent(Window origin, boolean entered);
}
