package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowMouseButtonEventListener {
    void onWindowMouseButtonEvent(Window origin, int button, int action, int mods);
}
