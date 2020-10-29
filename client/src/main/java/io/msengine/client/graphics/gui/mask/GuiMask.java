package io.msengine.client.graphics.gui.mask;

import io.msengine.client.graphics.gui.GuiManager;
import io.msengine.client.renderer.model.ModelHandler;

public abstract class GuiMask {
	
	protected GuiManager manager;
	protected ModelHandler model;
	
	public void innerInit(GuiManager manager) {
		this.manager = manager;
		this.model = manager.getModel();
		this.init();
	}
	
	public void innerStop() {
		this.stop();
		this.model = null;
		this.manager = null;
	}
	
	public GuiManager getManager() {
		return this.manager;
	}
	
	public ModelHandler getModel() {
		return this.model;
	}
	
	protected abstract void init();
	protected abstract void stop();
	
	public abstract void draw();
	
}
