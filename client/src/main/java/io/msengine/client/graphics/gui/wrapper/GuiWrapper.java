package io.msengine.client.graphics.gui.wrapper;

import io.msengine.client.graphics.gui.GuiObject;

public class GuiWrapper {

	protected final GuiObject inner;
	
	public GuiWrapper(GuiObject inner) {
		this.inner = inner;
	}
	
	public GuiObject getInner() {
		return this.inner;
	}

}
