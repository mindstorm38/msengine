package io.msengine.client.graphics.gui.event;

import io.msengine.client.graphics.gui.GuiObject;
import io.msengine.common.util.event.ObjectEventManager;

public class GuiEventManager extends ObjectEventManager<GuiEvent> {

    public void fireGuiEvent(GuiObject origin, GuiEvent event) {
        event.setOrigin(origin);
        this.fireEvent(event);
    }

}
