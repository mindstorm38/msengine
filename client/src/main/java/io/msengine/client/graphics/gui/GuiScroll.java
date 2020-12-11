package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.mask.GuiMask;
import io.msengine.client.graphics.gui.mask.GuiMaskRect;
import io.msengine.client.window.Window;
import io.msengine.client.window.listener.WindowScrollEventListener;
import org.lwjgl.glfw.GLFW;

public class GuiScroll extends GuiParent implements WindowScrollEventListener {

	private final Internal internal = new Internal();
	private final GuiMaskRect mask = new GuiMaskRect();
	
	private boolean xOverflowHidden = true;
	private boolean yOverflowHidden = true;
	
	private float xRatio, yRatio;
	private float xMaxScroll, yMaxScroll;
	private boolean mouseScrollEnabled = true;
	private float mouseScrollFactor = -10f;
	
	public GuiScroll() {
		
		this.mask.setAnchor(-1, -1);
		
		this.addChild(this.internal);
		this.addChild(this.mask);
		
	}
	
	@Override
	protected void init() {
		super.init();
		this.getWindow().addEventListener(WindowScrollEventListener.class, this);
	}
	
	@Override
	protected void stop() {
		super.stop();
		this.getWindow().removeEventListener(WindowScrollEventListener.class, this);
	}
	
	@Override
	protected void render(float alpha) {
		GuiMask.MaskTracker tracker = this.mask.mask();
		super.render(alpha);
		tracker.close();
	}
	
	@Override
	public void onWindowScrollEvent(Window origin, double xOffset, double yOffset) {
		if (this.mouseScrollEnabled && this.isCursorOver()) {
			if (this.doSwapScrollOffsets()) {
				double tmp = yOffset;
				yOffset = xOffset;
				xOffset = tmp;
			}
			if (xOffset != 0) this.addXScroll((float) xOffset * this.mouseScrollFactor);
			if (yOffset != 0) this.addYScroll((float) yOffset * this.mouseScrollFactor);
		}
	}
	
	protected boolean doSwapScrollOffsets() {
		return this.getWindow().getKey(GLFW.GLFW_KEY_LEFT_SHIFT) == Window.ACTION_PRESS ||
				this.getWindow().getKey(GLFW.GLFW_KEY_RIGHT_SHIFT) == Window.ACTION_PRESS;
	}
	
	@Override
	protected void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.updateXInfo();
		this.updateXOverflow();
	}
	
	@Override
	protected void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.updateYInfo();
		this.updateYOverflow();
	}
	
	// Scroll //
	
	public float getXMaxScroll() {
		return this.xMaxScroll;
	}
	
	public float getYMaxScroll() {
		return this.yMaxScroll;
	}
	
	public boolean canScrollOnX() {
		return this.xMaxScroll != 0;
	}
	
	public boolean canScrollOnY() {
		return this.yMaxScroll != 0;
	}
	
	public float getXRatio() {
		return this.xRatio;
	}
	
	public float getYRatio() {
		return this.yRatio;
	}
	
	public float getXScroll() {
		return -this.internal.getXPos();
	}
	
	public float getYScroll() {
		return -this.internal.getYPos();
	}
	
	public void addXScroll(float dxScroll) {
		if (dxScroll != 0) {
			this.setXScroll(this.getXScroll() + dxScroll);
		}
	}
	
	public void addYScroll(float dyScroll) {
		if (dyScroll != 0) {
			this.setYScroll(this.getYScroll() + dyScroll);
		}
	}
	
	public void setXScroll(float xScroll) {
		
		if (xScroll < 0) {
			xScroll = 0;
		} else if (xScroll > this.xMaxScroll) {
			xScroll = this.xMaxScroll;
		}
		
		this.internal.internalSetXPos(-xScroll);
		
	}
	
	public void setYScroll(float yScroll) {
		
		if (yScroll < 0) {
			yScroll = 0;
		} else if (yScroll > this.yMaxScroll) {
			yScroll = this.yMaxScroll;
		}
		
		this.internal.internalSetYPos(-yScroll);
		
	}
	
	public void setScroll(float xScroll, float yScroll) {
		this.setXScroll(xScroll);
		this.setYScroll(yScroll);
	}
	
	// Scroll Info //
	
	private void updateXInfo() {
		
		this.xRatio = this.internal.realWidth / this.realWidth;
		this.xMaxScroll = Math.max(this.internal.realWidth - this.realWidth, 0);
		
		if (this.getXScroll() > this.xMaxScroll)
			this.setXScroll(this.xMaxScroll);
		
	}
	
	private void updateYInfo() {
		
		this.yRatio = this.internal.realHeight / this.realHeight;
		this.yMaxScroll = Math.max(this.internal.realHeight - this.realHeight, 0);
		
		if (this.getYScroll() > this.yMaxScroll)
			this.setYScroll(this.yMaxScroll);
		
	}
	
	// Mouse Scroll //
	
	public boolean isMouseScrollEnabled() {
		return this.mouseScrollEnabled;
	}
	
	public void setMouseScrollEnabled(boolean enabled) {
		this.mouseScrollEnabled = enabled;
	}
	
	public float getMouseScrollFactor() {
		return this.mouseScrollFactor;
	}
	
	public void setMouseScrollFactor(float factor) {
		this.mouseScrollFactor = factor;
	}
	
	// Overflow Hidden //
	
	public void setXOverflow(boolean xHidden) {
		if (this.xOverflowHidden != xHidden) {
			this.xOverflowHidden = xHidden;
			this.updateXOverflow();
		}
	}
	
	public void setYOverflow(boolean yHidden) {
		if (this.yOverflowHidden != yHidden) {
			this.yOverflowHidden = yHidden;
			this.updateYOverflow();
		}
	}
	
	public void setOverflowHidden(boolean xHidden, boolean yHidden) {
		this.setXOverflow(xHidden);
		this.setYOverflow(yHidden);
	}
	
	private void updateXOverflow() {
		if (this.xOverflowHidden) {
			this.mask.setXPos(0);
			this.mask.setWidth(this.realWidth);
		} else {
			this.mask.setXPos(-2000);
			this.mask.setWidth(this.realWidth + 2000);
		}
	}
	
	private void updateYOverflow() {
		if (this.yOverflowHidden) {
			this.mask.setYPos(0);
			this.mask.setHeight(this.realHeight);
		} else {
			this.mask.setYPos(-2000);
			this.mask.setHeight(this.realHeight + 2000);
		}
	}
	
	// Internal //
	
	public GuiParent getInternal() {
		return this.internal;
	}
	
	public void resizeAutoInternal() {
		this.internal.resizeAuto();
	}
	
	private static void throwInternalUnsupported() {
		throw new UnsupportedOperationException("Not supported for scroll internal container.");
	}
	
	private class Internal extends GuiParent {
		
		private void internalSetXPos(float xPos) {
			super.setXPos(xPos);
			this.getManager().updateSceneCursorFromWindow();
		}
		
		private void internalSetYPos(float yPos) {
			super.setYPos(yPos);
			this.getManager().updateSceneCursorFromWindow();
		}
		
		@Override
		public void setXPos(float xPos) {
			throwInternalUnsupported();
		}
		
		@Override
		public void setYPos(float yPos) {
			throwInternalUnsupported();
		}
		
		@Override
		public void setXAnchor(float xAnchor) {
			throwInternalUnsupported();
		}
		
		@Override
		public void setYAnchor(float yAnchor) {
			throwInternalUnsupported();
		}
		
		@Override
		public void setXSupAnchor(float xSupAnchor) {
			throwInternalUnsupported();
		}
		
		@Override
		public void setYSupAnchor(float ySupAnchor) {
			throwInternalUnsupported();
		}
		
		@Override
		public float getAutoWidth() {
			return GuiScroll.this.realWidth;
		}
		
		@Override
		public float getAutoHeight() {
			return GuiScroll.this.realHeight;
		}
		
		@Override
		protected void onRealWidthChanged() {
			GuiScroll.this.updateXInfo();
		}
		
		@Override
		protected void onRealHeightChanged() {
			GuiScroll.this.updateYInfo();
		}
		
		public void resizeAuto() {
			
			float width = 0, height = 0;
			float currentWidth, currentHeight;
			
			for (GuiObject child : this.children) {
				currentWidth = child.getXOffsetFromParent() + child.getRealWidth();
				currentHeight = child.getYOffsetFromParent() + child.getRealHeight();
				if (currentWidth > width) width = currentWidth;
				if (currentHeight > height) height = currentHeight;
			}
			
			this.setSize(width, height);
			
		}
		
	}
	
}
