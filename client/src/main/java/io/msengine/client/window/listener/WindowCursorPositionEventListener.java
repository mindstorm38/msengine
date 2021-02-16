package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowCursorPositionEventListener {
	void onWindowCursorPositionEvent(Window origin, double x, double y);
}
