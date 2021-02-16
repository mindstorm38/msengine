package io.msengine.client.graphics.gui.wrapper;

import io.msengine.client.graphics.gui.GuiObject;

public class GuiWrapperCentered extends GuiWrapper {
	
	private float goalRatio = 1f;
	
	public GuiWrapperCentered(GuiObject inner) {
		super(inner);
	}
	
	public void setGoalRatio(float ratio) {
		this.goalRatio = ratio;
	}
	
	public float getGoalRatio() {
		return this.goalRatio;
	}
	
	public void setCenteredSize(float xOff, float yOff, float width, float height) {
		float ratio = width / height;
		this.inner.setAnchor(0, 0);
		this.inner.setPos(xOff + (width / 2), yOff + (height / 2));
		if (ratio > this.goalRatio) {
			this.inner.setSize(width, width / this.goalRatio);
		} else {
			this.inner.setSize(height * this.goalRatio, height);
		}
	}
	
}
