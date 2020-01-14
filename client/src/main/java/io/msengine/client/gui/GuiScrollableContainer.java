package io.msengine.client.gui;

import io.msengine.client.renderer.gui.GUIMaskRectangle;
import io.msengine.client.renderer.gui.GuiMask;
import io.msengine.client.renderer.window.Window;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import io.msengine.client.renderer.window.listener.WindowScrollEventListener;

public class GuiScrollableContainer extends GuiParent implements
		WindowMousePositionEventListener,
		WindowScrollEventListener  {

	private final InternalContainer internal;
	private final GUIMaskRectangle mask;
	private final GuiMask[] maskArray;
	
	private float xRatio = 0;
	private float yRatio = 0;
	
	private float maxScrollX = 0;
	private float maxScrollY = 0;
	
	private boolean mouseOver = false;
	
	private boolean mouseScrollEnabled = true;
	private float mouseScrollFactor = 10;
	
	public GuiScrollableContainer() {
		
		this.internal = new InternalContainer();
		this.addChild(this.internal);
		
		this.mask = new GUIMaskRectangle();
		this.maskArray = new GuiMask[] { this.mask };
		
	}
	
	// Overrides //
	
	@Override
	public void init() {
		
		super.init();
		
		Window.getInstance().addMousePositionEventListener(this);
		Window.getInstance().addScrollEventListener(this);
		
		this.mask.init();
		
	}
	
	@Override
	public void stop() {
		
		this.mask.stop();
		
		Window.getInstance().removeMousePositionEventListener(this);
		Window.getInstance().removeScrollEventListener(this);
		
		super.stop();
		
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.internal.updateScrollableWidth();
		this.updateXInfo();
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.internal.updateScrollableHeight();
		this.updateYInfo();
	}
	
	@Override
	public void render(float alpha) {
		
		this.renderer.mask(this.maskArray);
		super.render(alpha);
		this.renderer.unmask();
		
	}
	
	@Override
	public void updateXOffset() {
		
		super.updateXOffset();
		this.mask.setX(this.xOffset);
		this.mask.setWidth(this.width);
		
	}
	
	@Override
	public void updateYOffset() {
		
		super.updateYOffset();
		this.mask.setY(this.yOffset);
		this.mask.setHeight(this.height);
		
	}
	
	// Scroll //
	public float getMaxScrollX() {
		return this.maxScrollX;
	}
	
	public float getMaxScrollY() {
		return this.maxScrollY;
	}
	
	public boolean canScrollOnX() {
		return this.maxScrollX != 0;
	}
	
	public boolean canScrollOnY() {
		return this.maxScrollY != 0;
	}
	
	public float getScrollX() {
		return -this.internal.getXPos();
	}
	
	public float getScrollY() {
		return -this.internal.getYPos();
	}
	
	public void setScrollX(float x) {
		
		if (x < 0) x = 0;
		else if (x > this.maxScrollX) x = this.maxScrollX;
		
		this.internal.setXPos(-x);
		
	}
	
	public void setScrollY(float y) {
		
		if (y < 0) y = 0;
		else if (y > this.maxScrollY) y = this.maxScrollY;
		
		this.internal.setYPos(-y);
		
	}
	
	public void setScroll(float x, float y) {
		this.setScrollX(x);
		this.setScrollY(y);
	}
	
	public boolean isMouseScrollEnabled() {
		return mouseScrollEnabled;
	}
	
	public void setMouseScrollEnabled(boolean mouseScrollEnabled) {
		this.mouseScrollEnabled = mouseScrollEnabled;
	}
	
	public boolean isMouseOver() {
		return mouseOver;
	}
	
	public float getMouseScrollFactor() {
		return mouseScrollFactor;
	}
	
	public void setMouseScrollFactor(float mouseScrollFactor) {
		this.mouseScrollFactor = mouseScrollFactor;
	}
	
	// Update ratio //
	private void updateXInfo() {
		
		this.xRatio = this.internal.getWidth() / this.width;
		this.maxScrollX = Math.max(this.internal.getWidth() - this.width, 0);
		
		if (this.getScrollX() > this.maxScrollX)
			this.setScrollX(this.maxScrollX);
		
	}
	
	private void updateYInfo() {
		
		this.yRatio = this.internal.getHeight() / this.height;
		this.maxScrollY = Math.max(this.internal.getHeight() - this.height, 0);
		
		if (this.getScrollY() > this.maxScrollY)
			this.setScrollY(this.maxScrollY);
		
	}
	
	// Events listeners
	@Override
	public void windowMousePositionEvent(int x, int y) {
	
		if (this.renderable()) {
			this.mouseOver = this.isPointOver(x, y);
		} else if (this.mouseOver) {
			this.mouseOver = false;
		}
		
	}
	
	@Override
	public void windowScrollEvent(double scrollX, double scrollY) {
	
		if (this.mouseOver) {
			
			if (scrollX != 0) {
				this.setScrollX(this.getScrollX() + ((float) scrollX * this.mouseScrollFactor));
			}
			
			if (scrollY != 0) {
				this.setScrollY(this.getScrollY() - ((float) scrollY * this.mouseScrollFactor));
			}
			
		}
		
	}
	
	// Internal updated //
	private void internalWidthUpdated() {
		this.updateXInfo();
	}
	
	private void internalHeightUpdated() {
		this.updateYInfo();
	}
	
	public GuiParent getInternal() {
		return this.internal;
	}
	
	public boolean addInternalChild(GuiObject child) {
		return this.internal.addChild(child);
	}
	
	public boolean removeInternalChild(GuiObject child) {
		return this.internal.removeChild(child);
	}
	
	public void setInternalSize(float width, float height) {
		this.internal.setSize(width, height);
	}
	
	private class InternalContainer extends GuiParent {
		
		private float desiredWidth = 0;
		private float desiredHeight = 0;
		
		private InternalContainer() {
			super.setAnchor(-1, -1);
		}
		
		@Override
		public void setAnchor(float xAnchor, float yAnchor) {
			throw new UnsupportedOperationException("Can't set scrollable internal container anchor.");
		}
		
		@Override
		public void setWidth(float width) {
			this.desiredWidth = width;
			this.updateScrollableWidth();
		}
		
		@Override
		public void setHeight(float height) {
			this.desiredHeight = height;
			this.updateScrollableHeight();
		}
		
		private void updateScrollableWidth() {
			super.setWidth(Math.max(this.desiredWidth, GuiScrollableContainer.this.getWidth()));
			GuiScrollableContainer.this.internalWidthUpdated();
		}
		
		private void updateScrollableHeight() {
			super.setHeight(Math.max(this.desiredHeight, GuiScrollableContainer.this.getHeight()));
			GuiScrollableContainer.this.internalHeightUpdated();
		}
		
		@Override
		public float getAutoWidth() {
			return GuiScrollableContainer.this.getWidth();
		}
		
		@Override
		public float getAutoHeight() {
			return GuiScrollableContainer.this.getHeight();
		}
		
		/*
		@Override
		public void childXOffsetUpdated(GuiObject child) {
			
			super.childXOffsetUpdated(child);
			
			float requiredWidth = child.getOpositeOffsetX() - this.xOffset;
			
			if (requiredWidth > this.width)
				this.setWidth(requiredWidth);
			
		}
		
		@Override
		public void childYOffsetUpdated(GuiObject child) {
			
			super.childYOffsetUpdated(child);
			
			float requiredHeight = child.getOpositeOffsetY() - this.yOffset;
			
			if (requiredHeight > this.height)
				this.setHeight(requiredHeight);
			
		}
		
		@Override
		public boolean addChild(GuiObject child) {
			
			if (super.addChild(child)) {
				
				this.childXOffsetUpdated(child);
				this.childYOffsetUpdated(child);
				return true;
				
			} else {
				return false;
			}
			
		}
		*/
		
	}
	
}
