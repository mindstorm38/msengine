package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

/**
 * @deprecated Use {@link WindowCursorPositionEventListener} instead.
 */
@Deprecated
@FunctionalInterface
public interface WindowMousePositionEventListener {
	void onWindowMousePositionEvent(Window origin, int x, int y);
}
