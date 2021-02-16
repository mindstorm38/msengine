package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.event.GuiSceneResizedEvent;

public class GuiScene extends GuiParent {
	
	/**
	 * Called each time the scene is loaded
	 */
	protected void loaded() {}
	protected void unloaded() {}
	
	public GuiScene() {
		this.width = 0;
		this.height = 0;
	}
	
	@Override
	public void updateXOffset() {
		
		// Overridden to ignore offsets recalculation (default to 0)
		
		if (this.realWidth != this.width) {
			this.realWidth = this.width;
			this.onRealWidthChanged();
		}
		
		this.children.forEach(GuiObject::updateXOffset);
		
	}
	
	@Override
	public void updateYOffset() {
		
		// Overridden to ignore offsets recalculation (default to 0)
		
		if (this.realHeight != this.height) {
			this.realHeight = this.height;
			this.onRealHeightChanged();
		}
		
		this.children.forEach(GuiObject::updateYOffset);
		
	}
	
	void setSceneSize(int width, int height) {
		super.setWidth(width);
		super.setHeight(height);
		this.onSceneResized(width, height);
		this.fireEvent(new GuiSceneResizedEvent(width, height));
	}
	
	/**
	 * <p>Use this method insteadof adding a listener for {@link GuiSceneResizedEvent} event when listening in the scene itself.</p>
	 * <p><i>This method is called before firing {@link GuiSceneResizedEvent}.</i></p>
	 * <p>This method is still compatible with {@link #onRealWidthChanged()} (equals to {@link #onWidthChanged()}) and
	 * {@link #onRealHeightChanged()} (equals to {@link #onHeightChanged()}), <b>prefer these methods because this method
	 * can be deprecated in future versions</b>.</p>
	 * @param width The new scene width.
	 * @param height The new scene height.
	 */
	protected void onSceneResized(float width, float height) { }
	
	@Override
	public final void onXOffsetChanged() { }
	
	@Override
	public final void onYOffsetChanged() { }
	
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
	
	@Override
	public boolean isPointOver(float x, float y) {
		return true;
	}
	
}
