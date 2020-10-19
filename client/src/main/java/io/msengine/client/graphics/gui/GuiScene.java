package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.event.GuiSceneResizedEvent;

public class GuiScene extends GuiParent {
	
	/**
	 * Called each time the scene is loaded
	 */
	protected void loaded() {}
	protected void unloaded() {}
	
	@Override
	public void updateXOffset() {
		// Overridden to ignore offsets recalculation (default to 0)
		this.children.forEach(GuiObject::updateXOffset);
	}
	
	@Override
	public void updateYOffset() {
		// Overridden to ignore offsets recalculation (default to 0)
		this.children.forEach(GuiObject::updateYOffset);
	}
	
	void setSceneSize(int width, int height) {
		super.setWidth(width);
		super.setHeight(height);
		this.onSceneResized(width, height);
		this.fireEvent(new GuiSceneResizedEvent(width, height));
	}
	
	/**
	 * Use this method insteadof adding a listener for {@link GuiSceneResizedEvent} event when listening in the scene itself.<br>
	 * <i>This method is called before firing {@link GuiSceneResizedEvent}.</i>
	 * @param width The new scene width.
	 * @param height The new scene height.
	 */
	protected void onSceneResized(float width, float height) { }
	
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
