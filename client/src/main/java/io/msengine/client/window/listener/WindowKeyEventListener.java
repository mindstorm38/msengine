package io.msengine.client.window.listener;

import io.msengine.client.window.Window;

@FunctionalInterface
public interface WindowKeyEventListener {
    void onWindowKeyEvent(Window origin, int key, int scanCode, int action, int mods);
}
