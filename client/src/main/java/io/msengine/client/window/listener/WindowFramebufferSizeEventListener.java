package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowFramebufferSizeEventListener {
    void onWindowFramebufferSizeChangedEvent(Window origin, int width, int height);
}
