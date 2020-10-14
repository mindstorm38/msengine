package io.msengine.client.ngui.event;

import io.msengine.client.ngui.GuiObject;
import io.msengine.common.util.event.ObjectEventManager;

public class GuiEventManager extends ObjectEventManager<GuiEvent> {

    public void fireGuiEvent(GuiObject origin, GuiEvent event) {
        event.setOrigin(origin);
        this.fireEvent(event);
    }

}
