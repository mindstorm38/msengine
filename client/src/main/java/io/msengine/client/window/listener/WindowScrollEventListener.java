package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowScrollEventListener {
	void onWindowScrollEvent(Window origin, double xOffset, double yOffset);
}
