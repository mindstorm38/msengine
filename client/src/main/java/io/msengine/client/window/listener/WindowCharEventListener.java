package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowCharEventListener {
    void onWindowCharEvent(Window origin, char codePoint);
}
