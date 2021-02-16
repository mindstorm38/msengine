package io.msengine.client.graphics.gui.event;

import io.msengine.client.graphics.gui.GuiObject;
import io.msengine.common.util.event.ObjectEvent;

public class GuiEvent extends ObjectEvent {

    private GuiObject origin = null;

    void setOrigin(GuiObject origin) {
        this.origin = origin;
    }

    public GuiObject getOrigin() {
        return this.origin;
    }

}
