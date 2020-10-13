package io.msengine.client.ngui;

public class GuiScene extends GuiParent {

	public GuiScene() {
		super.setXAnchor(-1);
		super.setYAnchor(-1);
	}
	
	protected void loaded() {}
	protected void unloaded() {}
	
	void setSceneSize(int width, int height) {
		super.setWidth(width);
		super.setHeight(height);
		// TODO Fire scene resized event
	}
	
	@Override
	void setDisplayed(boolean displayed) {
		throw new UnsupportedOperationException("Can't set displayed for scene.");
	}
	
	@Override
	public void setVisible(boolean visible) {
		throw new UnsupportedOperationException("Can't set scene visible state.");
	}
	
	@Override
	public void setXPos(float xPos) {
		throw new UnsupportedOperationException("Can't set scene position.");
	}
	
	@Override
	public void setYPos(float yPos) {
		throw new UnsupportedOperationException("Can't set scene position.");
	}
	
	@Override
	public void setWidth(float width) {
		throw new UnsupportedOperationException("Can't set scene size.");
	}
	
	@Override
	public void setHeight(float height) {
		throw new UnsupportedOperationException("Can't set scene size.");
	}
	
	@Override
	public void setXAnchor(float xAnchor) {
		throw new UnsupportedOperationException("Can't set scene anchor.");
	}
	
	@Override
	public void setYAnchor(float yAnchor) {
		throw new UnsupportedOperationException("Can't set scene anchor.");
	}
	
	@Override
	void setParent(GuiParent parent) {
		throw new UnsupportedOperationException("Can't add a scene to a GuiParent.");
	}
	
}
