package io.msengine.client.gui;

import io.msengine.client.gui.event.GuiSceneResizedEvent;

@Deprecated
public abstract class GuiScene extends GuiParent {

	protected Class<? extends GuiScene> previousScene;
	
	public GuiScene() {
		
		super();
		
		super.setAnchor(-1f, -1f);
		
	}
	
	protected void loaded(Class<? extends GuiScene> previousScene) {
		
		this.previousScene = previousScene;
		this.setSceneActive(true);
		
	}
	
	protected void unloaded() {
		
		this.previousScene = null;
		this.setSceneActive(false);
		
	}
	
	@Override
	public void setAnchor(float xAnchor, float yAnchor) {
		throw new UnsupportedOperationException("Can't set scene position anchor.");
	}
	
	@Override
	public void setVisible(boolean visible) {
		throw new UnsupportedOperationException("Can't set visibility to a scene.");
	}
	
	@Override
	public void setSize(float width, float height) {
		throw new UnsupportedOperationException("Can't set scene size.");
	}
	
	@Override
	void setParent(GuiParent parent) {
		throw new UnsupportedOperationException("Can't add scene to a parent object.");
	}
	
	void setSceneSize(int width, int height) {
		super.setSize(width, height);
		this.fireEvent(new GuiSceneResizedEvent(width, height));
	}
	
}
